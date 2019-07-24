package io.protoless.messages.encoders

import scala.annotation.implicitNotFound

import shapeless.Nat

import io.protoless.messages.Encoder

@implicitNotFound("No IncrementalEncoder found for type ${A} and ${N}.")
trait IncrementalEncoder[A, N <: Nat] extends Encoder[A]

/**
  * Utilities for [[IncrementalEncoder]]
  */
final object IncrementalEncoder {

  def apply[A, N <: Nat](implicit instance: IncrementalEncoder[A, N]): IncrementalEncoder[A, N] = instance

}
