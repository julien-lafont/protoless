package io.protoless.encoders

import scala.annotation.implicitNotFound
import shapeless.Nat

import io.protoless.Encoder

@implicitNotFound("No IncrementalEncoder found for type ${A} and ${N}.")
@annotation.inductive
trait IncrementalEncoder[A, N <: Nat] extends Encoder[A]

/**
  * Utilities for [[IncrementalEncoder]]
  */
final object IncrementalEncoder {

  def apply[A, N <: Nat](implicit instance: IncrementalEncoder[A, N]): IncrementalEncoder[A, N] = instance

}
