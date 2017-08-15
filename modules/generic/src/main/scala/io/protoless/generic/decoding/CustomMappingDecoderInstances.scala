package io.protoless.generic.decoding

import com.google.protobuf.CodedInputStream

import shapeless.ops.nat.ToInt
import shapeless.{::, Generic, HList, HNil, Nat}

import io.protoless.messages.decoders.CustomMappingDecoder
import io.protoless.fields.FieldDecoder
import io.protoless.messages.Decoder.Result

trait CustomMappingDecoderInstances {

  implicit val decodeCustomMappingHNil: CustomMappingDecoder[HNil, HNil] = new CustomMappingDecoder[HNil, HNil] {
    override def decode(input: CodedInputStream): Result[HNil] = Right(HNil)
  }

  // Decode mapping specified with Nat (Nat._1 :: Nat._3 :: HNil)
  implicit def decodeCustomMappingHList[H, T <: HList, L <: Nat, TN <: HList](implicit
    hDecoder: FieldDecoder[H],
    index: ToInt[L],
    tDecoder: CustomMappingDecoder[T, TN]
  ): CustomMappingDecoder[H :: T, L :: TN] = new CustomMappingDecoder[H :: T, L :: TN] {
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
    tDecoder: CustomMappingDecoder[T, TN]
  ): CustomMappingDecoder[H :: T, L :: TN] = new CustomMappingDecoder[H :: T, L :: TN] {
    override def decode(input: CodedInputStream): Result[H :: T] = {
      for {
        h <- hDecoder.read(input, valueOf[L]).right
        t <- tDecoder.decode(input).right
      } yield h :: t
    }
  }

  implicit def decodeCustomMapping[A, L <: HList, R <: HList](implicit
    gen: Generic.Aux[A, R],
    decoder: CustomMappingDecoder[R, L]
  ): CustomMappingDecoder[A, L] = new CustomMappingDecoder[A, L] {
    override def decode(input: CodedInputStream): Result[A] = {
      decoder.decode(input) match {
        case Right(repr) => Right(gen.from(repr))
        case l @ Left(_) => l.asInstanceOf[Result[A]]
      }
    }
  }
}
