package io.protoless.grpc.client

import scala.concurrent.Promise

import io.grpc._
import io.protoless.tests.samples.{GreeterGrpc, Schemas}

object HelloWorldClient extends App with Utils {

  import scala.concurrent.ExecutionContext.Implicits.global

  val msg = args.lift(0).getOrElse("Hello")
  val channel = ManagedChannelBuilder.forAddress("127.0.0.1", 9100).usePlaintext(true).build()
  val stub = GreeterGrpc.newFutureStub(channel)

  val request = Schemas.Input.newBuilder().setMsg(msg).build()

  val response = stub.sayHello(request).asScala

  response.foreach { output =>
    print(output.getResponse)
  }

  scala.concurrent.Await.result(response, scala.concurrent.duration.Duration.Inf)

  val methodDescriptor: io.grpc.MethodDescriptor[Schemas.Input, Schemas.Output] = {
    io.grpc.MethodDescriptor.newBuilder[Schemas.Input, Schemas.Output]
      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
      .setFullMethodName(io.grpc.MethodDescriptor.generateFullMethodName("io.protoless.tests.samples.Greeter", "sayHello"))
      .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(Schemas.Input.getDefaultInstance))
      .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(Schemas.Output.getDefaultInstance))
      .build()
  }


  class UnaryStringToFuture[RespT] extends ClientCall.Listener[RespT] {
    private val promise = Promise[RespT]()

    override def onHeaders(headers: Metadata): Unit = super.onHeaders(headers)

    override def onClose(status: Status, trailers: Metadata): Unit = super.onClose(status, trailers)

    override def onMessage(message: RespT): Unit = super.onMessage(message)

    override def onReady(): Unit = super.onReady()
  }
  /*
  private static class UnaryStreamToFuture<RespT> extends ClientCall.Listener<RespT> {
    private final GrpcFuture<RespT> responseFuture;
    private RespT value;

    public UnaryStreamToFuture(GrpcFuture<RespT> responseFuture) {
      this.responseFuture = responseFuture;
    }

    @Override
    public void onHeaders(Metadata headers) {
    }

    @Override
    public void onMessage(RespT value) {
      if (this.value != null) {
        throw Status.INTERNAL.withDescription("More than one value received for unary call")
            .asRuntimeException();
      }
      this.value = value;
    }

    @Override
    public void onClose(Status status, Metadata trailers) {
      if (status.isOk()) {
        if (value == null) {
          // No value received so mark the future as an error
          responseFuture.setException(
              Status.INTERNAL.withDescription("No value received for unary call")
                  .asRuntimeException(trailers));
        }
        responseFuture.set(value);
      } else {
        responseFuture.setException(status.asRuntimeException(trailers));
      }
    }
  }
   */

  io.grpc.stub.ClientCalls.asyncUnaryCall(
    channel.newCall(methodDescriptor, CallOptions.DEFAULT), request

  )


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