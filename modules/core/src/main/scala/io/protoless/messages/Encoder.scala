package io.protoless

import scala.annotation.implicitNotFound
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

import com.google.protobuf.CodedOutputStream

import io.protoless.wrappers.ProtolessOutputStream

/**
  * Interface for all Encoder implementations.
  *
  * Allows to encode instances of `A` as protobuf3 serialized message.
  *
  * Encoding can be done with `Automatic` strategy with [[encoders.AutoEncoder]], or by specifying
  * a custom protobuf mapping with [[encoders.CustomMappingEncoder]].
  */
@implicitNotFound("No Encoder found for type ${A}.")
trait Encoder[A] extends Serializable { self =>

  /**
    * Write the value A in the protobuf OutputStream.
    */
  def encode(a: A, output: CodedOutputStream): Unit

  /**
    * Encode the value A into an ByteArrayOutputStream.
    */
  def encodeAsStream(a: A): ByteArrayOutputStream = {
    val out = new ByteArrayOutputStream()
    val cos = CodedOutputStream.newInstance(out)
    encode(a, cos)
    out
  }

  /**
    * Encode the value A and return the result in Array[Byte].
    */
  def encodeAsBytes(a: A): Array[Byte] = {
    encodeAsStream(a).toByteArray
  }

  /**
    * Encode the value A and return the result as a ByteBuffer.
    */
  def encodeAsByteBuffer(a: A): ByteBuffer = {
    ByteBuffer.wrap(encodeAsBytes(a))
  }

  /**
    * Create a new [[Encoder]] by applying a function to a value of type `B` before writing as an A.
    */
  final def contramap[B](f: B => A): Encoder[B] = new Encoder[B] {
    final def encode(b: B, output: CodedOutputStream): Unit = self.encode(f(b), output)
  }

}

/**
  * Utilities for [[Encoder]].
  */
final object Encoder {

  /**
    * Return an instance for a given type `A`.
    */
  def apply[A](implicit instance: Encoder[A]): Encoder[A] = instance

  /**
    * Construct an instance from a function.
    */
  def instance[A](f: A => ProtolessOutputStream => Unit): Encoder[A] = new Encoder[A] {
    override def encode(a: A, output: CodedOutputStream): Unit = {
      f(a)(new ProtolessOutputStream(output))
      output.flush()
    }
  }

}
