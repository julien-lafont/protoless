package io.protoless.grpc.server

import io.grpc.{ServerBuilder, ServerServiceDefinition}
import io.grpc.stub.StreamObserver
import io.protoless.tests.samples.Schemas.Output
import io.protoless.tests.samples.{GreeterGrpc, Schemas}
/*
class GenericBase extends io.grpc.BindableService {
  override def bindService(): ServerServiceDefinition = {

  }
}
*/
class HelloWorldServer extends GreeterGrpc.GreeterImplBase {

  override def sayHello(request: Schemas.Input, responseObserver: StreamObserver[Schemas.Output]): Unit = {
    val reply = Output.newBuilder().setResponse(request.getMsg + "world").build()
    responseObserver.onNext(reply)
    responseObserver.onCompleted()
  }
}

object HelloWorldServerBootstrap extends App {

  val server = ServerBuilder.forPort(9100).addService(new HelloWorldServer()).build()
  server.start()
  print("Server started listening on port 9100\n")

  sys.addShutdownHook {
    val _ = server.shutdown()
  }


  server.awaitTermination()


}