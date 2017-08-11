package io.protoless

import java.io.ByteArrayOutputStream

package object syntax {

  implicit final class EncoderOps[A](val wrappedEncodeable: A) extends AnyVal {
    final def asProtobufBytes(implicit encoder: Encoder[A]): Array[Byte] = encoder.encodeAsBytes(wrappedEncodeable)

    final def asProtobufStream(implicit encoder: Encoder[A]): ByteArrayOutputStream = encoder.encodeAsStream(wrappedEncodeable)
  }

}
