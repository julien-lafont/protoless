package io.protoless.generic.encoding

import com.google.protobuf.CodedOutputStream

import shapeless.{::, Generic, HList, HNil, Nat}
import shapeless.ops.nat.ToInt

import io.protoless.fields.FieldEncoder

trait CustomMappingEncoderInstances extends IncrementalEncoderInstances {

  implicit val encodeCustomMappingHNil: DerivedCustomMappingEncoder[HNil, HNil] = new DerivedCustomMappingEncoder[HNil, HNil] {
    override def encode(a: HNil, output: CodedOutputStream): Unit = {}
  }

  // Encode mapping specified with Nat (Nat._1 :: Nat._3 :: HNil)
  implicit def encodeCustomMappingHList[H, T <: HList, L <: Nat, TN <: HList](implicit
    hEncoder: FieldEncoder[H],
    index: ToInt[L],
    tEncoder: DerivedCustomMappingEncoder[T, TN]
  ): DerivedCustomMappingEncoder[H :: T, L :: TN] = new DerivedCustomMappingEncoder[H :: T, L :: TN] {
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
    tEncoder: DerivedCustomMappingEncoder[T, TN]
  ): DerivedCustomMappingEncoder[H :: T, L :: TN] = new DerivedCustomMappingEncoder[H :: T, L :: TN] {
    override def encode(a: H :: T, output: CodedOutputStream): Unit = {
      val (h :: t) = a
      hEncoder.write(valueOf[L], h, output)
      tEncoder.encode(t, output)
    }
  }

  implicit def encodeCustomMapping[A, L <: HList, R <: HList](implicit
    gen: Generic.Aux[A, R],
    encoder: DerivedCustomMappingEncoder[R, L]
  ): DerivedCustomMappingEncoder[A, L] = new DerivedCustomMappingEncoder[A, L] {
    override def encode(a: A, output: CodedOutputStream): Unit = {
      encoder.encode(gen.to(a), output)
      output.flush()
    }
  }

}
