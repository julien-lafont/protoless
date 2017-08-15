package io.protoless.wrappers

import com.google.protobuf.CodedInputStream

import io.protoless.Decoder
import io.protoless.fields.FieldDecoder

/**
  * Wrapper around protobuf `CodedInputStream` introducing runtime methods to read fields
  * from a protobuf stream.
  */
final class ProtolessInputStream(private val input: CodedInputStream) {

  private val lastIndex = new java.util.concurrent.atomic.AtomicInteger(0)

  /**
    * Read value located at index `index` as an `A`.
    */
  final def read[A](index: Int)(implicit decoder: FieldDecoder[A]): Decoder.Result[A] = {
    assert(index > lastIndex.get(),
      s"You cannot read fields in reverse order. Last index read was ${lastIndex.get()}, "+
      s"and you tried to read at position $index (${decoder.toString}")

    lastIndex.set(index)
    decoder.read(input, index)
  }

  /**
    * Read the next value from the protobuf stream as an `A`.
    *
    * We will read the field located at position `last index read + 1`.
    */
  final def read[A](implicit decoder: FieldDecoder[A]): Decoder.Result[A] = {
    val index = lastIndex.incrementAndGet()
    decoder.read(input, index)
  }
}
