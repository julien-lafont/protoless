package io.protoless.generic.decoding

import com.google.protobuf.CodedInputStream
import shapeless.{::, Generic, HList, HNil, Nat, Succ}

import io.protoless.messages.Decoder.Result
import io.protoless.fields.FieldDecoder

trait IncrementalDecoderInstances {

  implicit def decodeIncrementalHNil[N <: Nat]: DerivedIncrementalDecoder[HNil, N] = new DerivedIncrementalDecoder[HNil, N] {
    override def decode(input: CodedInputStream): Result[HNil] = Right(HNil)
  }

  implicit def decodeIncrementalHList[H, T <: HList, N <: Nat](implicit
    hDecoder: FieldDecoder[H],
    index: shapeless.ops.nat.ToInt[N],
    tDecoder: DerivedIncrementalDecoder[T, Succ[N]]
  ): DerivedIncrementalDecoder[H :: T, N] = new DerivedIncrementalDecoder[H :: T, N] {
    override def decode(input: CodedInputStream): Result[H :: T] = {
      for {
        h <- hDecoder.read(input, index()).right
        t <- tDecoder.decode(input).right
      } yield h :: t
    }
  }

  implicit def decodeIncremental[A, N <: Nat, R <: HList](implicit
    gen: Generic.Aux[A, R],
    decoder: DerivedIncrementalDecoder[R, N]
  ): DerivedIncrementalDecoder[A, N] = new DerivedIncrementalDecoder[A, N] {
    override def decode(input: CodedInputStream): Result[A] = {
      decoder.decode(input) match {
        case Right(repr) => Right(gen.from(repr))
        case l @ Left(_) => l.asInstanceOf[Result[A]]
      }
    }
  }
}
