package io.protoless.core.decoders

import scala.annotation.implicitNotFound
import shapeless.HList

import io.protoless.core.Decoder

/**
  * A type class that decode a value of type `A` from a `CodedInputSteam`.
  * Each parameter of `A` is associated with an index defined in the `L` HList.
  *
  * The first parameter of `A` is associated with the first `Nat` in `L`,
  * the second parameter of `A` with the second `Nat` in `L`, etc.
  *
  * This allows to decode protobuf message with non successive fields number.
  */
@implicitNotFound("No CustomMappingDecoder found for type ${A} and ${L}.")
@annotation.inductive
trait CustomMappingDecoder[A, L <: HList] extends Decoder[A]

/**
  * Utilities for [[CustomMappingDecoder]]
  */
final object CustomMappingDecoder {

  def apply[A, L <: HList](implicit instance: CustomMappingDecoder[A, L]): CustomMappingDecoder[A, L] = instance

}

