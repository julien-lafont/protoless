package io.protoless.messages.encoders

import scala.annotation.implicitNotFound

import io.protoless.messages.Encoder

@implicitNotFound("No AutoEncoder found for type ${A}.")
trait AutoEncoder[A] extends Encoder[A]

/**
  * Utilities for [[AutoEncoder]]
  */
final object AutoEncoder {

  def apply[A](implicit instance: AutoEncoder[A]): AutoEncoder[A] = instance

}
