package io.protoless.generic.encoding

import com.google.protobuf.CodedOutputStream
import shapeless.{::, Generic, HList, HNil, Nat, Succ}
import shapeless.ops.nat.ToInt

import io.protoless.fields.FieldEncoder

trait IncrementalEncoderInstances {

  implicit def encodeIncrementalHNil[N <: Nat]: DerivedIncrementalEncoder[HNil, N] = new DerivedIncrementalEncoder[HNil, N] {
    override def encode(a: HNil, output: CodedOutputStream): Unit = {}
  }

  implicit def encodeIncrementalHList[H, T <: HList, N <: Nat](implicit
    hEncoder: FieldEncoder[H],
    index: ToInt[N],
    tEncoder: DerivedIncrementalEncoder[T, Succ[N]]
  ): DerivedIncrementalEncoder[H :: T, N] = new DerivedIncrementalEncoder[H :: T, N] {
    override def encode(a: H :: T, output: CodedOutputStream): Unit = {
      val (h :: t) = a
      hEncoder.write(index(), h, output)
      tEncoder.encode(t, output)
    }
  }

  implicit def encodeIncremental[A, N <: Nat, R <: HList](implicit
    gen: Generic.Aux[A, R],
    encoder: DerivedIncrementalEncoder[R, N]
  ): DerivedIncrementalEncoder[A, N] = new DerivedIncrementalEncoder[A, N] {
    override def encode(a: A, output: CodedOutputStream): Unit = {
      encoder.encode(gen.to(a), output)
      output.flush()
    }
  }

}
