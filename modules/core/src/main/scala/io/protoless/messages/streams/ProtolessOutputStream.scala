package io.protoless.messages.streams

import com.google.protobuf.CodedOutputStream

import io.protoless.fields.FieldEncoder

/**
  * Wrapper around protobuf `CodedOuputStream` introducing runtime methods to write fields
  * in a protobuf stream.
  */
final class ProtolessOutputStream(private val output: CodedOutputStream) {

  private val lastIndex = new java.util.concurrent.atomic.AtomicInteger(0)

  /**
    * Write the field `A` into field number `index`.
    */
  final def write[A](a: A, index: Int)(implicit encoder: FieldEncoder[A]): Unit = {
    assert(index > lastIndex.get(),
      s"You cannot write fields in reverse order. Last index written was ${lastIndex.get()}, "+
        s"and you tried to write at position $index (${encoder.toString}")

    lastIndex.set(index)
    encoder.write(index, a, output)
  }

  final def write[A](a: A)(implicit encoder: FieldEncoder[A]): Unit = {
    val index = lastIndex.incrementAndGet()
    encoder.write(index, a, output)
  }

  // TODO: generate `write` methods with 2..22 parameters
}
