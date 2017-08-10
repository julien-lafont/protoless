package io.protoless.core.encoders

import scala.annotation.implicitNotFound

import io.protoless.core.Encoder

@implicitNotFound("No AutoEncoder found for type ${A}.")
@annotation.inductive
trait AutoEncoder[A] extends Encoder[A]

/**
  * Utilities for [[AutoEncoder]]
  */
final object AutoEncoder {

  def apply[A](implicit instance: AutoEncoder[A]): AutoEncoder[A] = instance

}
