package io.protoless.generic.decoding

import com.google.protobuf.CodedInputStream
import shapeless.{Generic, HList, Nat}

import io.protoless.messages.Decoder.Result
import io.protoless.messages.decoders.{AutoDecoder, IncrementalDecoder}

trait AutoDecoderInstances extends IncrementalDecoderInstances with CustomMappingDecoderInstances {

  implicit def decodeAuto[A, R <: HList](implicit
    gen: Generic.Aux[A, R],
    decoder: IncrementalDecoder[R, Nat._1]
  ): AutoDecoder[A] = new AutoDecoder[A] {
    override def decode(input: CodedInputStream): Result[A] = {
      decoder.decode(input) match {
        case Right(repr) => Right(gen.from(repr))
        case l @ Left(_) => l.asInstanceOf[Result[A]]
      }
    }
  }

}
