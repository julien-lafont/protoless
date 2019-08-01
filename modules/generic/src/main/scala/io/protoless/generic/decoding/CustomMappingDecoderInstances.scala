package io.protoless.generic.decoding

import com.google.protobuf.CodedInputStream

import shapeless.ops.nat.ToInt
import shapeless.{::, Generic, HList, HNil, Nat}

import io.protoless.fields.FieldDecoder
import io.protoless.messages.Decoder.Result

trait CustomMappingDecoderInstances extends IncrementalDecoderInstances {

  implicit val decodeCustomMappingHNil: DerivedCustomMappingDecoder[HNil, HNil] = new DerivedCustomMappingDecoder[HNil, HNil] {
    override def decode(input: CodedInputStream): Result[HNil] = Right(HNil)
  }

  // Decode mapping specified with Nat (Nat._1 :: Nat._3 :: HNil)
  implicit def decodeCustomMappingHList[H, T <: HList, L <: Nat, TN <: HList](implicit
    hDecoder: FieldDecoder[H],
    index: ToInt[L],
    tDecoder: DerivedCustomMappingDecoder[T, TN]
  ): DerivedCustomMappingDecoder[H :: T, L :: TN] = new DerivedCustomMappingDecoder[H :: T, L :: TN] {
    override def decode(input: CodedInputStream): Result[H :: T] = {
      for {
        h <- hDecoder.read(input, index()).right
        t <- tDecoder.decode(input).right
      } yield h :: t
    }
  }

  // Decode mapping specified with Literal types (-Yliteral-types) (1 :: 3 :: HNil)
  implicit def decodeCustomMappingHListLiteral[H, T <: HList, L <: Int, TN <: HList](implicit
    hDecoder: FieldDecoder[H],
    index: ValueOf[L],
    tDecoder: DerivedCustomMappingDecoder[T, TN]
  ): DerivedCustomMappingDecoder[H :: T, L :: TN] = new DerivedCustomMappingDecoder[H :: T, L :: TN] {
    override def decode(input: CodedInputStream): Result[H :: T] = {
      for {
        h <- hDecoder.read(input, valueOf[L]).right
        t <- tDecoder.decode(input).right
      } yield h :: t
    }
  }

  implicit def decodeCustomMapping[A, L <: HList, R <: HList](implicit
    gen: Generic.Aux[A, R],
    decoder: DerivedCustomMappingDecoder[R, L]
  ): DerivedCustomMappingDecoder[A, L] = new DerivedCustomMappingDecoder[A, L] {
    override def decode(input: CodedInputStream): Result[A] = {
      decoder.decode(input) match {
        case Right(repr) => Right(gen.from(repr))
        case l @ Left(_) => l.asInstanceOf[Result[A]]
      }
    }
  }
}
