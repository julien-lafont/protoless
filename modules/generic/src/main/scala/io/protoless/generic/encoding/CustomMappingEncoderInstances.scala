package io.protoless.generic.encoding

import com.google.protobuf.CodedOutputStream

import shapeless.{::, Generic, HList, HNil, Nat}
import shapeless.ops.nat.ToInt

import io.protoless.core.encoders.CustomMappingEncoder
import io.protoless.core.fields.FieldEncoder

trait CustomMappingEncoderInstances {

  implicit val encodeCustomMappingHNil: CustomMappingEncoder[HNil, HNil] = new CustomMappingEncoder[HNil, HNil] {
    override def encode(a: HNil, output: CodedOutputStream): Unit = {}
  }

  // Encode mapping specified with Nat (Nat._1 :: Nat._3 :: HNil)
  implicit def encodeCustomMappingHList[H, T <: HList, L <: Nat, TN <: HList](implicit
    hEncoder: FieldEncoder[H],
    index: ToInt[L],
    tEncoder: CustomMappingEncoder[T, TN]
  ): CustomMappingEncoder[H :: T, L :: TN] = new CustomMappingEncoder[H :: T, L :: TN] {
    override def encode(a: H :: T, output: CodedOutputStream): Unit = {
      val (h :: t) = a
      hEncoder.write(index(), h, output)
      tEncoder.encode(t, output)
    }
  }

  // Encode mapping specified with Literal (1 :: 3 :: HNil)
  implicit def encodeCustomMappingHListLiteral[H, T <: HList, L <: Int, TN <: HList](implicit
    hEncoder: FieldEncoder[H],
    index: ValueOf[L],
    tEncoder: CustomMappingEncoder[T, TN]
  ): CustomMappingEncoder[H :: T, L :: TN] = new CustomMappingEncoder[H :: T, L :: TN] {
    override def encode(a: H :: T, output: CodedOutputStream): Unit = {
      val (h :: t) = a
      hEncoder.write(valueOf[L], h, output)
      tEncoder.encode(t, output)
    }
  }

  implicit def encodeCustomMapping[A, L <: HList, R <: HList](implicit
    gen: Generic.Aux[A, R],
    encoder: CustomMappingEncoder[R, L]
  ): CustomMappingEncoder[A, L] = new CustomMappingEncoder[A, L] {
    override def encode(a: A, output: CodedOutputStream): Unit = {
      encoder.encode(gen.to(a), output)
      output.flush()
    }
  }

}
