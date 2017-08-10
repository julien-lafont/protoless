package io.protoless.core

import scala.annotation.implicitNotFound

import com.google.protobuf.CodedOutputStream

/**
  * Interface for all Encoder implementations.
  */
@implicitNotFound("No Encoder found for type ${A}.")
trait Encoder[A] extends Serializable { self =>

  /**
    * Write the value A in the protobuf OutputStream.
    */
  def encode(a: A, output: CodedOutputStream): Unit

  /**
    * Create a new [[Encoder]] by applying a function to a value of type `B` before writing as an A.
    */
  final def contramap[B](f: B => A) = new Encoder[B] {
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
    *
    * @param writeOperation how to write the object `A` in the `CodedOutputStream`.
    */
  def instance[A](writeOperation: A => CodedOutputStream => Unit) = new Encoder[A] {
    override def encode(a: A, output: CodedOutputStream): Unit = writeOperation(a)(output)
  }

  /**
    * Construct an Encoder that writes a constant value in the `CodedOutputStream`.
    */
  def const[T](v: CodedOutputStream => Unit): Encoder[T] = instance[T](_ => output => v(output))
}
