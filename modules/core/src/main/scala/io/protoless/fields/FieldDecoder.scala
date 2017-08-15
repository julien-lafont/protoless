package io.protoless.fields

import scala.annotation.implicitNotFound
import scala.collection.mutable
import scala.collection.generic.CanBuildFrom
import scala.util.{Failure, Success, Try}

import com.google.protobuf.{ByteString, WireFormat, CodedInputStream => CIS}
import com.google.protobuf.WireFormat.FieldType
import shapeless.Unwrapped

import cats.data.NonEmptyList
import io.protoless.tag
import io.protoless.messages.Decoder.Result
import io.protoless.error.{DecodingFailure, MissingField, WrongFieldType}
import io.protoless.messages.Decoder
import io.protoless.tag._

/**
  * A type class that reads a single field `A` from a `CodedInputSteam`.
  */
@implicitNotFound("No FieldDecoder found for type ${A}.")
trait FieldDecoder[A] extends Serializable { self =>

  /**
    * Read value located at index `index` as an object of type `A` from the CodedInputStream `input`.
    */
  def read(input: CIS, index: Int): Result[A]

  /**
    * Read value located at index `index` as an object of type `A` from an Array[Byte].
    */
  final def decode(input: Array[Byte], index: Int): Result[A] = {
    read(CIS.newInstance(input), index)
  }

  /**
    * Map a function over this [[FieldDecoder]].
    */
  def map[B](f: A => B): FieldDecoder[B] = new FieldDecoder[B] {
    override def read(input: CIS, index: Int): Result[B] = {
      self.read(input, index) match {
        case Right(a) => Right(f(a))
        case l @ Left(_) => l.asInstanceOf[Result[B]]
      }
    }
  }

  /**
    * Monadically bind a function over this [[FieldDecoder]].
    */
  def flatMap[B](f: A => FieldDecoder[B]): FieldDecoder[B] = new FieldDecoder[B] {
    override def read(input: CIS, index: Int): Result[B] = {
      self.read(input, index) match {
        case Right(a) => f(a).read(input, index)
        case l @ Left(_) => l.asInstanceOf[Result[B]]
      }
    }
  }

  /**
    * Create a new decoder that performs some operation on the result if this one succeeds.
    *
    * @param f a function returning either a value or an error message
    */
  def emapTry[B](f: A => Try[B]): FieldDecoder[B] = new FieldDecoder[B] {
    override def read(input: CIS, index: Int): Result[B] = self.read(input, index) match {
      case Right(a) => f(a) match {
        case Success(b) => Right(b)
        case Failure(ex) => Left(DecodingFailure.fromThrowable(ex, index))
      }
      case l @ Left(_) => l.asInstanceOf[Result[B]]
    }
  }

  /**
    * Create a new decoder that performs some operation on the result if this one succeeds.
    *
    * @param f a function returning either a value or an error message
    */
  def emap[B](f: A => Result[B]): FieldDecoder[B] = new FieldDecoder[B] {
    override def read(input: CIS, index: Int): Result[B] = self.read(input, index) match {
      case Right(a) => f(a)
      case l @ Left(_) => l.asInstanceOf[Result[B]]
    }
  }


  /**
    * Build a new instance with the specified error message.
    */
  def withErrorMessage(message: String): FieldDecoder[A] = new FieldDecoder[A] {
    override def read(input: CIS, index: Int): Result[A] = self.read(input, index) match {
      case r @ Right(_) => r
      case Left(e) => Left(e.withMessage(message))
    }
  }

}

/**
  * A type class that reads a single field `A` from a `CodedInputSteam`.
  * This type can safely be read from a `repeated` field.
  */
@implicitNotFound(
  "No RepeatableFieldDecoder found for type ${A}."
)
trait RepeatableFieldDecoder[A] extends FieldDecoder[A] { self =>
  /**
    * Type of the target protobuf field, required to choose the decoding strategy (packed, length-delimited).
    */
  def fieldType: FieldType

  /**
    * Map a function over this [[FieldDecoder]].
    */
  final override def map[B](f: A => B): RepeatableFieldDecoder[B] = new RepeatableFieldDecoder[B] {
    override def read(input: CIS, index: Int): Result[B] = {
      self.read(input, index) match {
        case Right(a) => Right(f(a))
        case l @ Left(_) => l.asInstanceOf[Result[B]]
      }
    }

    override def fieldType: FieldType = self.fieldType
  }

  /**
    * Monadically bind a function over this [[FieldDecoder]].
    */
  final override def flatMap[B](f: A => FieldDecoder[B]): RepeatableFieldDecoder[B] = new RepeatableFieldDecoder[B] {
    override def read(input: CIS, index: Int): Result[B] = {
      self.read(input, index) match {
        case Right(a) => f(a).read(input, index)
        case l @ Left(_) => l.asInstanceOf[Result[B]]
      }
    }
    override def fieldType: FieldType = self.fieldType
  }

  /**
    * Create a new decoder that performs some operation on the result if this one succeeds.
    *
    * @param f a function returning either a value or an error message
    */
  final override def emapTry[B](f: A => Try[B]): RepeatableFieldDecoder[B] = new RepeatableFieldDecoder[B] {
    override def read(input: CIS, index: Int): Result[B] = self.read(input, index) match {
      case Right(a) => f(a) match {
        case Success(b) => Right(b)
        case Failure(ex) => Left(DecodingFailure.fromThrowable(ex, index))
      }
      case l @ Left(_) => l.asInstanceOf[Result[B]]
    }
    override def fieldType: FieldType = self.fieldType
  }

  /**
    * Build a new instance with the specified error message.
    */
  final override def withErrorMessage(message: String): RepeatableFieldDecoder[A] = new RepeatableFieldDecoder[A] {
    override def read(input: CIS, index: Int): Result[A] = self.read(input, index) match {
      case r @ Right(_) => r
      case Left(e) => Left(e.withMessage(message))
    }

    override def fieldType: FieldType = self.fieldType
  }
}

/**
  * Utilities and instances for [[FieldDecoder]].
  */
object FieldDecoder extends MidPriorityFieldDecoder {

  /**
    * Return a FieldDecoder instance for a given type `A`.
    *
    * @group Utilities
    */
  final def apply[A](implicit instance: FieldDecoder[A]): FieldDecoder[A] = instance

  /**
    * Generate a FieldDecoder that always return a single value.
    *
    * @group Utilities
    */
  final def const[A](a: A): FieldDecoder[A] = new FieldDecoder[A] {
    override def read(input: CIS, index: Int): Result[A] = Right(a)
  }

  /**
    * Generate a FieldDecoder that always return a single failure.
    *
    * @group Utilities
    */
  final def failed[A](failure: String): FieldDecoder[A] = new FieldDecoder[A] {
    override def read(input: CIS, index: Int): Result[A] = Left(DecodingFailure(failure))
  }

  /**
    * Generate a field decoder for a native protobuf type
    *
    * @group Utilities
    */
  final private[protoless] def native[A](r: CIS => A, expectedType: FieldType): RepeatableFieldDecoder[A] = new RepeatableFieldDecoder[A] {
    override def fieldType: FieldType = expectedType
    override def read(input: CIS, index: Int): Result[A] = {
      val tag = readTag(input, index)

      // Check that the fieldNumber match the current index
      if (tag.fieldNumber != index) Left(MissingField(index, expectedType, tag.wireType, tag.fieldNumber))
      // Check if the type match, except if fieldType=2 for repeatedfields
      else if (tag.wireType != 2 && tag.wireType != expectedType.getWireType) Left(WrongFieldType(expectedType, tag.fieldNumber, tag.wireType))
      // else try to decode the input
      else Try(r(input)) match {
        case Success(b) => Right(b)
        case Failure(ex) => Left(DecodingFailure.fromThrowable(ex, index))
      }
    }
  }

  /**
    * @group DecodingNative
    */
  implicit final val decodeDouble: RepeatableFieldDecoder[Double] = native(_.readDouble, FieldType.DOUBLE)

  /**
    * @group DecodingNative
    */
  implicit final val decodeFloat: RepeatableFieldDecoder[Float] = native(_.readFloat(), FieldType.FLOAT)

  /**
    * @group DecodingNative
    */
  implicit final val decodeUInt: RepeatableFieldDecoder[Int @@ Unsigned] = native(cis => tag.unsigned(cis.readUInt32()), FieldType.UINT32)

  /**
    * @group DecodingNative
    */
  implicit final val decodeSInt: RepeatableFieldDecoder[Int @@ Signed] = native(cis => tag.signed(cis.readSInt32()), FieldType.SINT32)

  /**
    * @group DecodingNative
    */
  implicit final val decodeFInt: RepeatableFieldDecoder[Int @@ Fixed] = native(cis => tag.fixed(cis.readFixed32()), FieldType.FIXED32)

  /**
    * @group DecodingNative
    */
  implicit final val decodeSFInt: RepeatableFieldDecoder[Int @@ Signed with Fixed] = native(cis => tag.signedFixed(cis.readSFixed32()), FieldType.SFIXED32)

  /**
    * @group DecodingNative
    */
  implicit final val decodeInt: RepeatableFieldDecoder[Int] = native(_.readInt32(), FieldType.INT32)

  /**
    * @group DecodingNative
    */
  implicit final val decodeULong: RepeatableFieldDecoder[Long @@ Unsigned] = native(cis => tag.unsigned(cis.readUInt64()), FieldType.UINT64)

  /**
    * @group DecodingNative
    */
  implicit final val decodeSLong: RepeatableFieldDecoder[Long @@ Signed] = native(cis => tag.signed(cis.readSInt64()), FieldType.SINT64)

  /**
    * @group DecodingNative
    */
  implicit final val decodeFLong: RepeatableFieldDecoder[Long @@ Fixed] = native(cis => tag.fixed(cis.readFixed64()), FieldType.FIXED64)

  /**
    * @group DecodingNative
    */
  implicit final val decodeSFLong: RepeatableFieldDecoder[Long @@ Signed with Fixed] = native(cis => tag.signedFixed(cis.readSFixed64()), FieldType.SFIXED64)

  /**
    * @group DecodingNative
    */
  implicit final val decodeLong: RepeatableFieldDecoder[Long] = native(_.readInt64(), FieldType.INT64)

  /**
    * @group DecodingNative
    */
  implicit final val decodeBoolean: RepeatableFieldDecoder[Boolean] = native(_.readBool(), FieldType.BOOL)

  /**
    * @group DecodingNative
    */
  implicit final val decodeString: RepeatableFieldDecoder[String] = native(_.readStringRequireUtf8(), FieldType.STRING)

  /**
    * @group DecodingNative
    */
  implicit final val decodeByteString: RepeatableFieldDecoder[ByteString] = native(_.readBytes(), FieldType.BYTES)

  /**
    * Decode an UUID from a `repeated sint64` field, containing the `mostSignificantBits`
    * and `LeastSignificantBits` of the 128 bits UUID.
    *
    * @group Decoding
    */
  implicit final val decodeUUID: FieldDecoder[java.util.UUID] = {
    apply[List[Long @@ Signed]]
      .withErrorMessage("UUID must be encoded as `repeated sint64`")
      .emap {
        case m :: l :: Nil => Right(new java.util.UUID(m, l))
        case other: Any => Left(DecodingFailure(s"$other must have size 2 to create an UUID from long's"))
      }
  }

  /**
    * @group Decoding
    */
  implicit final val decodeBigDecimal: RepeatableFieldDecoder[BigDecimal] = decodeString.emapTry(v => Try(BigDecimal(v)))

  /**
    * @group Decoding
    */
  implicit final val decodeBigInt: RepeatableFieldDecoder[BigInt] = decodeString.emapTry(v => Try(BigInt(v)))

  /**
    * @group Decoding
    */
  implicit final val decodeShort: RepeatableFieldDecoder[Short] = decodeInt.map(_.toShort)

  /**
    * @group Decoding
    */
  implicit final val decodeChar: RepeatableFieldDecoder[Char] = decodeInt.map(_.toChar)

  /**
    * Generate a FieldDecoder allowing to parse Enumerations.
    *
    * {{{
    *   object WeekDay extends Enumeration { ... }
    *   implicit val weekDayDecoder: RepeatableFieldDecoder[WeekDay.Value] = FieldDecoder.decodeEnum(WeekDay)
    * }}}
    *
    * @group Decoding
    */
  final def decodeEnum[E <: Enumeration](enum: E): RepeatableFieldDecoder[E#Value] = decodeInt.emapTry(v =>
    Try(enum(v)).orElse(Failure(new DecodingFailure(s"Cannot find value at index $v in enum $enum")))
  )


  /**
    * @group Decoding
    */
  implicit final def decodeOption[A](implicit dec: RepeatableFieldDecoder[A]): RepeatableFieldDecoder[Option[A]] = new RepeatableFieldDecoder[Option[A]] {
    override def read(input: CIS, index: Int): Result[Option[A]] = {
      val tag = readTag(input, index)
      if (tag.fieldNumber == index) dec.read(input, index).right.map(Some.apply)
      else Right(None)
    }
    override def fieldType: FieldType = dec.fieldType
  }

  /**
    * Automatically decode a value wrapped in a value class
    *
    * @group Decoding
    */
  implicit final def decodeValueClass[A, R](implicit
    ev: A <:< AnyVal,
    unwrapped: Unwrapped.Aux[A, R],
    dec: FieldDecoder[R]
  ): FieldDecoder[A] = dec.map(unwrapped.wrap)

  /**
    * @group Collection
    */
  implicit final def decodeNonEmptyList[A](implicit dec: FieldDecoder[List[A]]): FieldDecoder[NonEmptyList[A]] = dec.flatMap {
    case head :: tail => const(NonEmptyList(head, tail))
    case _ => failed("NonEmptyList cannot be empty")
  }
}

trait MidPriorityFieldDecoder extends LowPriorityFieldDecoder {

  /**
    * Allow to decode a message `A` as a nested field if we have a `Decoder[A]`.
    *
    * @group Decoding
    */
  implicit final def decodeNestedMessage[A](implicit dec: Decoder[A]): RepeatableFieldDecoder[A] = new RepeatableFieldDecoder[A] {
    override def read(input: CIS, index: Int): Result[A] = {
      val tag = readTag(input, index)
      if (tag.wireType == WireFormat.FieldType.MESSAGE.getWireType)  {

        val messageBytes = input.readByteArray()
        dec.decode(messageBytes)
      } else {
        Left(DecodingFailure(s"Try to read nested message (with wireType=${WireFormat.FieldType.MESSAGE.getWireType}, but found ${tag.wireType}."))
      }
    }

    /**
      * Type of the target protobuf field, required to choose the decoding strategy (packed, length-delimited).
      */
    override def fieldType: FieldType = FieldType.MESSAGE
  }

}

trait LowPriorityFieldDecoder {

  /**
    * Decode a repeating field of type `A` into a collection of high-kind type `C`.
    *
    * @group Collection
    */
  implicit final def decodeCanBuildFrom[A, C[_]](implicit dec: RepeatableFieldDecoder[A], cbf: CanBuildFrom[Nothing, A, C[A]]): RepeatableFieldDecoder[C[A]] = new RepeatableFieldDecoder[C[A]] {

    @scala.annotation.tailrec
    private def readUntilFieldNumberEquals(index: Int, input: CIS, cbf: mutable.Builder[A, C[A]]): Either[DecodingFailure, C[A]] = {
      dec.read(input, index) match {
        case l @ Left(_) => l.asInstanceOf[Either[DecodingFailure, C[A]]]
        case Right(v) =>
          cbf += v

          if (readTag(input, Integer.MIN_VALUE).fieldNumber != index)
            Right(cbf.result())
          else
            readUntilFieldNumberEquals(index, input, cbf)
      }
    }

    @scala.annotation.tailrec
    private def readUntilLimit(index: Int, input: CIS, cbf: mutable.Builder[A, C[A]]): Either[DecodingFailure, C[A]] = {
      if (input.getBytesUntilLimit == 0) Right(cbf.result())
      else {
        dec.read(input, index) match {
          case l @ Left(_) => l.asInstanceOf[Either[DecodingFailure, C[A]]]
          case Right(v) =>
            cbf += v
            readUntilLimit(index, input, cbf)
        }
      }
    }

    override def read(input: CIS, index: Int): Result[C[A]] = {

      val tag = readTag(input, index)

      if (tag.fieldNumber > index) Right(cbf.apply().result())
      else {
        if (dec.fieldType.getWireType == WireFormat.WIRETYPE_LENGTH_DELIMITED) {
          readUntilFieldNumberEquals(index, input, cbf.apply())
        } else {
          val size = input.readRawVarint32()
          val limit = input.pushLimit(size)
          val result = readUntilLimit(index, input, cbf.apply())
          input.popLimit(limit)
          result
        }

      }
    }

    override def fieldType: FieldType = dec.fieldType
  }

  /**
    * Read the next field tag and extract the FieldNumber / WireType.
    *
    * Detect if the tag has already been read by checking the `lastTag` in priority.
    * If the field number read from the lastTag is different that the currentIndex, so read the next tag from the stream.
    */
  final protected[protoless] def readTag(input: CIS, currentIndex: Int): FieldTag = {
    val lastTag = input.getLastTag
    val lastIndex = WireFormat.getTagFieldNumber(lastTag)

    if (currentIndex == lastIndex) FieldTag(lastTag)
    else FieldTag(input.readTag())
  }

  protected[protoless] case class FieldTag private(fieldNumber: Int, wireType: Int)
  protected[protoless] object FieldTag {
    def apply(tag: Int): FieldTag = {
      FieldTag(WireFormat.getTagFieldNumber(tag), WireFormat.getTagWireType(tag))
    }
  }


}

