package io.protoless.fields

import java.io.ByteArrayOutputStream
import java.util.UUID

import com.google.protobuf.{ByteString, CodedInputStream, CodedOutputStream}
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import io.protoless.instances.ArbitraryInstances
import io.protoless.tag._
import io.protoless.tests.ProtolessSuite

case class Budget(budget: Long) extends AnyVal
case class Id(value: UUID) extends AnyVal

class FieldEncodingCheck extends ProtolessSuite with GeneratorDrivenPropertyChecks with ArbitraryInstances {

  "Field encoding must pass property testing" - {

    "Int" in {
      check[Int]
    }

    "Long" in {
      check[Long]
    }

    "Float" in {
      check[Float]
    }

    "Double" in {
      check[Double]
    }

    "String" in {
      check[String]
    }

    "Boolean" in {
      check[Boolean]
    }

    "ByteString" in {
      check[ByteString]
    }

    "unsigned int32" in {
      check[Int @@ Unsigned]
    }

    "unsigned int64" in {
      check[Long @@ Unsigned]
    }

    "signed int32" in {
      check[Int @@ Signed]
    }

    "signed int64" in {
      check[Long @@ Signed]
    }

    "fixed int32" in {
      check[Int @@ Fixed]
    }

    "fixed int64" in {
      check[Long @@ Fixed]
    }

    "fixed signed int32" in {
      check[Int @@ Signed with Fixed]
    }

    "fixed signed int64" in {
      check[Long @@ Signed with Fixed]
    }

    "Short" in {
      check[Short]
    }

    "Char" in {
      check[Char]
    }

    "BigDecimal" in {
      check[BigDecimal]
    }

    "BigInt" in {
      check[BigInt]
    }

    "UUID" in {
      check[UUID]
    }

    "Option[Int]" in {
      check[Option[Int]]
    }

    "Option[String]" in {
      check[Option[String]]
    }

    "Budget (ValueClass Long)" in {
      implicit val budget: Arbitrary[Budget] = Arbitrary(arbitrary[Long].map(Budget.apply))
      check[Budget]
    }

    "id (ValueClass String)" in {
      implicit val id: Arbitrary[Id] = Arbitrary(arbitraryUUID.arbitrary.map(Id.apply))
      check[Id]
    }

  }

  val indexGenerator: Gen[Int] = Gen.choose(1, 1000)

  private def check[T](implicit enc: FieldEncoder[T], dec: FieldDecoder[T], arbitrary: Arbitrary[T]) = {
    forAll(indexGenerator) { index =>
      forAll((t: T) =>
        encodeDecodeField(t, index)
      )
    }
  }

  private def encodeDecodeField[T](t: T, index: Int)(implicit enc: FieldEncoder[T], dec: FieldDecoder[T]) = {
    val bytesEncoded = {
      val out = new ByteArrayOutputStream()
      val cos = CodedOutputStream.newInstance(out)
      enc.write(index, t, cos)
      cos.flush()
      out.toByteArray
    }

    val entityDecoded = {
      val cis = CodedInputStream.newInstance(bytesEncoded)
      dec.read(cis, index).right.get
    }

    val entityReEncoded = {
      val out = new ByteArrayOutputStream()
      val cos = CodedOutputStream.newInstance(out)
      enc.write(index, entityDecoded, cos)
      cos.flush()
      out.toByteArray
    }

    entityReEncoded.toList == bytesEncoded.toList
  }
}
