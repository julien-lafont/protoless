package io.protoless.decoders

import scala.annotation.implicitNotFound
import shapeless.Nat

import io.protoless.Decoder

/**
  * A type class that decode a value of type `A` from a `CodedInputSteam`.
  *
  * Each parameters of `A` is associated to an incremental index (starting from `N`)
  * representing the protobuf field index.
  */
@implicitNotFound("No IncrementalDecoder found for type ${A} and ${N}.")
@annotation.inductive
trait IncrementalDecoder[A, N <: Nat] extends Decoder[A]

/**
  * Utilities for [[IncrementalDecoder]]
  */
final object IncrementalDecoder {

  def apply[A, N <: Nat](implicit instance: IncrementalDecoder[A, N]): IncrementalDecoder[A, N] = instance

}
