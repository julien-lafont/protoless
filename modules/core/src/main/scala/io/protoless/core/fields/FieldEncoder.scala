package io.protoless.core.fields

import scala.annotation.implicitNotFound
import java.io.ByteArrayOutputStream

import com.google.protobuf.WireFormat.FieldType
import com.google.protobuf.{ByteString, WireFormat, CodedOutputStream => COS}

import cats.data.NonEmptyList
import io.protoless.core.tag._

/**
  * A type class that write a single field `A` into a `CodedOutputStream`.
  */
@implicitNotFound("No FieldEncoder found for type ${A}.")
trait FieldEncoder[A] extends Serializable { self =>

  /**
    * Write the field `A` into the protobuf OutputStream with field number `index`.
    */
  def write(index: Int, a: A, output: COS): Unit

  /**
    * Create a new [[FieldEncoder]] by applying a function to a value of type `B` before writing as an A.
    */
  def contramap[B](f: B => A): FieldEncoder[B] = new FieldEncoder[B] {
    override def write(index: Int, a: B, output: COS): Unit = self.write(index, f(a), output)
  }
}

/**
  * A type class that write a single field `A` into a `CodedOutputStream`.
  * This type can safely be written into a `repeated` field.
  */
@implicitNotFound("No RepeatableFieldEncoder found for type ${A}.")
trait RepeatableFieldEncoder[A] extends FieldEncoder[A] { self =>

  /**
    * Write the field `A` in a repeated field (generally without its tag).
    */
  def writeRepeated(a: A, output: COS): Unit

  /**
    * Type of the target protobuf field, required to choose the encoding strategy (packed, length-delimited).
    */
  def fieldType: FieldType

  /**
    * Create a new [[RepeatableFieldEncoder]] by applying a function to a value of type `B` before writing as an A.
    */
  final override def contramap[B](f: B => A): RepeatableFieldEncoder[B] = new RepeatableFieldEncoder[B] {
    override def write(index: Int, a: B, output: COS): Unit = self.write(index, f(a), output)
    override def writeRepeated(a: B, output: COS): Unit = self.writeRepeated(f(a), output)
    override def fieldType: FieldType = self.fieldType
  }
}

/**
  * Utilities and instances for [[FieldEncoder]].
  *
  * @groupname Utilities Defining encoders instances
  * @groupprio Utilities 0
  *
  * @groupname EncodingNative Encoders for native protobuf fields
  * @groupprio EncodingNative 1
  *
  * @groupname Encoding Encoders for common types
  * @groupprio Encoding 2
  *
  * @groupname Collection Encoders for collections
  * @groupprio Collection 3
  */
object FieldEncoder extends MidPriorityFieldEncoder {

  /**
    * Return a FieldEncoder instance for a given type `A`.
    *
    * @group Utilities
    */
  final def apply[A](instance: FieldEncoder[A]): FieldEncoder[A] = instance

  /**
    * Return a RepeatableFieldEncoder instance for a given type `A`.
    *
    * @group Utilities
    */
  final def repeatable[A](instance: RepeatableFieldEncoder[A]): RepeatableFieldEncoder[A] = instance


  /**
    * @group EncodingNative
    */
  implicit final val encodeInt: RepeatableFieldEncoder[Int] = native(_.writeInt32, _.writeInt32NoTag, FieldType.INT32)

  /**
    * @group EncodingNative
    */
  implicit final val encodeUnsignedInt: RepeatableFieldEncoder[Int @@ Unsigned] = native(_.writeUInt32, _.writeUInt32NoTag, FieldType.UINT32)

  /**
    * @group EncodingNative
    */
  implicit final val encodeSignedInt: RepeatableFieldEncoder[Int @@ Signed] = native(_.writeSInt32, _.writeSInt32NoTag, FieldType.SINT32)

  /**
    * @group EncodingNative
    */
  implicit final val encodeFixedInt: RepeatableFieldEncoder[Int @@ Fixed] = native(_.writeFixed32, _.writeFixed32NoTag, FieldType.FIXED32)

  /**
    * @group EncodingNative
    */
  implicit final val encodeSignedFixedInt: RepeatableFieldEncoder[Int @@ Signed with Fixed] = native(_.writeSFixed32, _.writeSFixed32NoTag, FieldType.SFIXED32)

  /**
    * @group EncodingNative
    */
  implicit final val encodeLong: RepeatableFieldEncoder[Long] = native(_.writeInt64, _.writeInt64NoTag, FieldType.INT64)

  /**
    * @group EncodingNative
    */
  implicit final val encodeUnSignedLong: RepeatableFieldEncoder[Long @@ Unsigned] = native(_.writeUInt64, _.writeUInt64NoTag, FieldType.UINT64)

  /**
    * @group EncodingNative
    */
  implicit final val encodeSignedLong: RepeatableFieldEncoder[Long @@ Signed] = native(_.writeSInt64, _.writeSInt64NoTag, FieldType.SINT64)

  /**
    * @group EncodingNative
    */
  implicit final val encodeFixedLong: RepeatableFieldEncoder[Long @@ Fixed] = native(_.writeFixed64, _.writeFixed64NoTag, FieldType.FIXED64)

  /**
    * @group EncodingNative
    */
  implicit final val encodeSFixedLong: RepeatableFieldEncoder[Long @@ Signed with Fixed] = native(_.writeSFixed64, _.writeSFixed64NoTag, FieldType.SFIXED64)

  /**
    * @group EncodingNative
    */
  implicit final val encodeBoolean: RepeatableFieldEncoder[Boolean] = native(_.writeBool, _.writeBoolNoTag, FieldType.BOOL)

  /**
    * @group EncodingNative
    */
  implicit final val encodeDouble: RepeatableFieldEncoder[Double] = native(_.writeDouble, _.writeDoubleNoTag, FieldType.DOUBLE)

  /**
    * @group EncodingNative
    */
  implicit final val encodeFloat: RepeatableFieldEncoder[Float] = native(_.writeFloat, _.writeFloatNoTag, FieldType.FLOAT)

  /**
    * @group EncodingNative
    */
  implicit final val encodeString: RepeatableFieldEncoder[String] = native(_.writeString, _.writeStringNoTag, FieldType.STRING)

  /**
    * @group EncodingNative
    */
  implicit final val encodeByteString: RepeatableFieldEncoder[ByteString] = native(_.writeBytes, _.writeBytesNoTag, FieldType.BYTES)

  /**
    * @group EncodingNative
    */
  implicit final def encodeEnum[E <: Enumeration]: RepeatableFieldEncoder[E#Value] = new RepeatableFieldEncoder[E#Value] {
    override def writeRepeated(a: E#Value, output: COS): Unit = encodeInt.writeRepeated(a.id, output)
    override def write(index: Int, a: E#Value, output: COS): Unit = a.id match {
      case 0 => // First enum value is assumed by convention, not written
      case _ => encodeInt.write(index, a.id, output)
    }
    override val fieldType: FieldType = FieldType.ENUM
  }

  /**
    * @group Encoding
    */
  implicit final val encodeUUID: RepeatableFieldEncoder[java.util.UUID] = encodeString.contramap[java.util.UUID](_.toString)

  /**
    * @group Encoding
    */
  implicit final val encodeBigDecimal: RepeatableFieldEncoder[BigDecimal] = encodeString.contramap[BigDecimal](_.toString())

  /**
    * @group Encoding
    */
  implicit final val encodeBigInt: RepeatableFieldEncoder[BigInt] = encodeString.contramap[BigInt](_.toString())

  /**
    * @group Encoding
    */
  implicit final val encodeShort: RepeatableFieldEncoder[Short] = encodeInt.contramap[Short](_.toInt)

  /**
    * @group Encoding
    */
  implicit final val encodeChar: RepeatableFieldEncoder[Char] = encodeInt.contramap[Char](_.toInt)

  /**
    * @group Encoding
    */
  implicit final def encodeOption[A](implicit enc: RepeatableFieldEncoder[A]): RepeatableFieldEncoder[Option[A]] = new RepeatableFieldEncoder[Option[A]] {
    override def write(index: Int, a: Option[A], output: COS): Unit = a.foreach(enc.write(index, _, output))
    override def writeRepeated(a: Option[A], output: COS): Unit = a.foreach(enc.writeRepeated(_, output))
    override val fieldType: FieldType = enc.fieldType
  }

}

trait MidPriorityFieldEncoder extends LowPriorityFieldEncoder {


  private final def deriveFromTraversable[A, C[A] <: Traversable[A]](implicit enc: RepeatableFieldEncoder[A]): FieldEncoder[C[A]] = {
    encodeTraversable[A].contramap[C[A]](identity)
  }

  /**
    * @group Collection
    */
  implicit final def encodeSeq[A](implicit enc: RepeatableFieldEncoder[A]): FieldEncoder[Seq[A]] = deriveFromTraversable

  /**
    * @group Collection
    */
  implicit final def encodeImmSeq[A](implicit enc: RepeatableFieldEncoder[A]): FieldEncoder[collection.immutable.Seq[A]] = deriveFromTraversable

  /**
    * @group Collection
    */
  implicit final def encodeList[A](implicit enc: RepeatableFieldEncoder[A]): FieldEncoder[List[A]] = deriveFromTraversable

  /**
    * @group Collection
    */
  implicit final def encodeIterable[A](implicit enc: RepeatableFieldEncoder[A]): FieldEncoder[Iterable[A]] = deriveFromTraversable

  /**
    * @group Collection
    */
  implicit final def encodeVector[A](implicit enc: RepeatableFieldEncoder[A]): FieldEncoder[Vector[A]] = deriveFromTraversable

  /**
    * @group Collection
    */
  implicit final def encodeStream[A](implicit enc: RepeatableFieldEncoder[A]): FieldEncoder[Stream[A]] = deriveFromTraversable

  /**
    * @group Collection
    */
  implicit final def encodeArray[A](implicit enc: RepeatableFieldEncoder[A]): FieldEncoder[Array[A]] = encodeTraversable(enc).contramap[Array[A]](_.toTraversable)

  /**
    * @group Collection
    */
  implicit final def encodeNonEmptyList[A](implicit enc: RepeatableFieldEncoder[A]): FieldEncoder[NonEmptyList[A]] = encodeTraversable(enc).contramap[NonEmptyList[A]](_.toList)

}

trait LowPriorityFieldEncoder extends FieldEncoderHelpers {

  /**
    * @group Collection
    */
  implicit final def encodeTraversable[A](implicit enc: RepeatableFieldEncoder[A]): FieldEncoder[Traversable[A]] = new FieldEncoder[Traversable[A]] {
    override def write(index: Int, list: Traversable[A], output: COS): Unit = {
      if (list.nonEmpty) { // empty collections are not written

        // Packable fields are written as one unique "length delimited" field
        // Values are encoded without their tag, one after the other, like a "big array byte"
        // The big array byte is written to the output, preceded by the total size
        if (enc.fieldType.isPackable) {

          // Prepare an internal output channel where all values will be written
          // It's required to known the total size of all values
          // FIXME: Could be calculated without internal buffer if packed field type is known (or atomic size is known)

          val subOutput = new ByteArrayOutputStream()
          val cos = COS.newInstance(subOutput)
          list.foreach(enc.writeRepeated(_, cos))
          cos.flush()
          val subOutputBytes = subOutput.toByteArray

          // Write index + WireType: 2 (length delimited)
          output.writeTag(index, WireFormat.WIRETYPE_LENGTH_DELIMITED)
          // Write raw byte array following it's size
          output.writeByteArrayNoTag(subOutputBytes)
        }

        // Non package fields are written one after the other by repeating the tag
        else {
          list.foreach(enc.write(index, _, output))
        }
      }
    }
  }

}

trait FieldEncoderHelpers {

  /**
    * Generate a field encoder for a native protobuf type
    *
    * @group Utilities
    */
  final protected def native[A](nativeWrite: COS => (Int, A) => Unit, nativeWriteRepeated: COS => A => Unit, nativeFieldType: FieldType): RepeatableFieldEncoder[A] = new RepeatableFieldEncoder[A] {
    override def write(index: Int, a: A, output: COS): Unit = {
      nativeWrite(output)(index, a)
    }

    override def writeRepeated(a: A, output: COS): Unit = {
      nativeWriteRepeated(output)(a)
    }

    override def fieldType: FieldType = nativeFieldType
  }

}
