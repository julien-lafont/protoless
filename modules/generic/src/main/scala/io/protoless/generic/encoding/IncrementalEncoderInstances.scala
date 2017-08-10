package io.protoless.generic.encoding

import com.google.protobuf.CodedOutputStream
import shapeless.{::, Generic, HList, HNil, Nat, Succ}
import shapeless.ops.nat.ToInt

import io.protoless.core.encoders.IncrementalEncoder
import io.protoless.core.fields.FieldEncoder

trait IncrementalEncoderInstances {

  implicit def encodeIncrementalHNil[N <: Nat]: IncrementalEncoder[HNil, N] = new IncrementalEncoder[HNil, N] {
    override def encode(a: HNil, output: CodedOutputStream): Unit = {}
  }

  implicit def encodeIncrementalHList[H, T <: HList, N <: Nat](implicit
    hEncoder: FieldEncoder[H],
    index: ToInt[N],
    tEncoder: IncrementalEncoder[T, Succ[N]]
  ): IncrementalEncoder[H :: T, N] = new IncrementalEncoder[H :: T, N] {
    override def encode(a: H :: T, output: CodedOutputStream): Unit = {
      val (h :: t) = a
      hEncoder.write(index(), h, output)
      tEncoder.encode(t, output)
    }
  }

  implicit def encodeIncremental[A, N <: Nat, R <: HList](implicit
    gen: Generic.Aux[A, R],
    encoder: IncrementalEncoder[R, N]
  ): IncrementalEncoder[A, N] = new IncrementalEncoder[A, N] {
    override def encode(a: A, output: CodedOutputStream): Unit = {
      encoder.encode(gen.to(a), output)
      output.flush()
    }
  }

}
