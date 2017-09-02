package io.protoless.grpc.client

import scala.concurrent.Promise
import scala.util.control.NonFatal
import java.io.{ByteArrayInputStream, InputStream}
import java.lang.ref.{Reference, WeakReference}

import com.google.protobuf.CodedInputStream
import shapeless.{HNil, ::, Nat}

import io.grpc._
import io.grpc.MethodDescriptor.Marshaller
import io.grpc.internal.GrpcUtil
import io.protoless.messages.{Decoder, Encoder}
import io.protoless.messages.Decoder.Result
import io.protoless.tests.samples.{GreeterGrpc, Schemas}

class UnaryStreamToFuture[RespT](promise: Promise[RespT]) extends ClientCall.Listener[RespT] {
  @volatile private var value: Option[RespT] = None

  override def onMessage(message: RespT): Unit = {
    if (value.isDefined) throw Status.INTERNAL.withDescription("More than one value received for unary call").asRuntimeException()
    value = Some(message)
  }

  override def onClose(status: Status, trailers: Metadata): Unit = {
    if (status.isOk) {
      value match {
        case Some(response) =>
          promise.success(response)
        case None =>
          promise.failure(Status.INTERNAL.withDescription("No value received for unary call")
            .asRuntimeException(trailers))
      }
    } else {
      promise.failure(status.asRuntimeException(trailers))
    }
  }
}

object HelloWorldClient extends App with Utils {

  import scala.concurrent.ExecutionContext.Implicits.global

  val msg = args.lift(0).getOrElse("Hello")
  val channel = ManagedChannelBuilder.forAddress("127.0.0.1", 9100).usePlaintext(true).build()
  val stub = GreeterGrpc.newFutureStub(channel)

  val request = Schemas.Input.newBuilder().setMsg(msg).build()

  import io.protoless._
  import io.protoless.generic.semiauto._

  case class In(msg: String)
  case class Out(msg: String)

  val inMarshaller = new Marshaller[In] {
    val encoderInput: Encoder[In] = deriveEncoder[In]
    val decoderInput: Decoder[In] = deriveDecoder[In]
    override def parse(stream: InputStream): In = decoderInput.decode(stream).right.get
    override def stream(value: In): InputStream = new ByteArrayInputStream(encoderInput.encodeAsBytes(value))
  }

  private val bufs = new ThreadLocal[Reference[Array[Byte]]]() {
    override protected def initialValue = new WeakReference[Array[Byte]](new Array[Byte](4096)) // Picked at random.
  }

  val outMarshaller = new Marshaller[Out] {
    val encoderOutput: Encoder[Out] = deriveEncoder[Out, Nat._2 :: HNil]
    val decoderOutput: Decoder[Out] = deriveDecoder[Out, Nat._2 :: HNil]
    
    override def parse(stream: InputStream): Out = {
      val result = stream match {
        case kl: KnownLength =>
          val size = kl.available
          if (size > 0 && size <= GrpcUtil.DEFAULT_MAX_MESSAGE_SIZE) { // buf should not be used after this method has returned.
            var buf = bufs.get.get
            if (buf == null || buf.length < size) {
              buf = new Array[Byte](size)
              bufs.set(new WeakReference[Array[Byte]](buf))
            }

            var chunkSize = 0
            var position = 0
            while ( {
              chunkSize = kl.read(buf, position, size - position)
              chunkSize != -1
            }) position += chunkSize

            if (size != position) throw new RuntimeException("size inaccurate: " + size + " != " + position)

            decoderOutput.decode(CodedInputStream.newInstance(buf, 0, size))
          }
          else if (size == 0) {
            throw new RuntimeException("Cannot decode empty objet, need a default instance not yet impmented")
          } else {
            decoderOutput.decode(kl)
          }
        case _ =>
          decoderOutput.decode(stream)
      }
      result.right.get
    }
    override def stream(value: Out) = new ByteArrayInputStream(encoderOutput.encodeAsBytes(value))
  }

  val methodDescriptor: io.grpc.MethodDescriptor[In, Out] = {
    io.grpc.MethodDescriptor.newBuilder[In, Out]
      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
      .setFullMethodName(io.grpc.MethodDescriptor.generateFullMethodName("io.protoless.tests.samples.Greeter", "SayHello"))
      .setRequestMarshaller(inMarshaller)
      .setResponseMarshaller(outMarshaller)
      .build()
  }

  def asyncUnaryRequestCall(
    call: ClientCall[In, Out],
    param: In,
    responseListener: ClientCall.Listener[Out],
    streamingResponse: Boolean): Unit = {

    // startCall
    call.start(responseListener, new Metadata)
    if (streamingResponse) call.request(1)
    else call.request(2)

    try {
      call.sendMessage(param)
      call.halfClose()
    } catch {
      case ex: RuntimeException =>
        call.cancel(null, ex)
        throw ex
      case throwable: Throwable =>
        call.cancel(null, throwable)
        throw new RuntimeException(throwable)
    }
  }

  val promise = Promise[Out]()
  val responseObserver: UnaryStreamToFuture[Out] = new UnaryStreamToFuture(promise)

  asyncUnaryRequestCall(
    channel.newCall(methodDescriptor, CallOptions.DEFAULT),
    In("coucou"),
    responseObserver,
    false
  )

  val response = promise.future

  response.foreach { output =>
    print(output)
  }

  scala.concurrent.Await.result(response, scala.concurrent.duration.Duration.Inf)



}

trait Utils {
  import com.google.common.util.concurrent.{ListenableFuture, Futures, FutureCallback}
  import scala.concurrent.{Promise, Future}
  import scala.language.implicitConversions

  implicit class RichListenableFuture[T](lf: ListenableFuture[T]) {
    def asScala: Future[T] = {
      val p = Promise[T]()
      Futures.addCallback(lf, new FutureCallback[T] {
        def onFailure(t: Throwable): Unit = p failure t
        def onSuccess(result: T): Unit    = p success result
      })
      p.future
    }
  }
}
