package io.protoless.generic.encoding

import com.google.protobuf.CodedOutputStream
import shapeless.{Generic, HList, Nat}

import io.protoless.messages.encoders.{AutoEncoder, IncrementalEncoder}

trait AutoEncoderInstances extends IncrementalEncoderInstances with CustomMappingEncoderInstances {

  implicit def encodeAuto[A, R <: HList](implicit
    gen: Generic.Aux[A, R],
    encoder: IncrementalEncoder[R, Nat._1]
  ): AutoEncoder[A] = new AutoEncoder[A] {
    override def encode(a: A, output: CodedOutputStream): Unit = {
      encoder.encode(gen.to(a), output)
      output.flush()
    }
  }

}
