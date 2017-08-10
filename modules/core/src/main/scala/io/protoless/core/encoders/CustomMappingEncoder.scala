package io.protoless.core.encoders

import scala.annotation.implicitNotFound
import shapeless.HList

import io.protoless.core.Encoder

@implicitNotFound("No CustomMappingEncoder found for type ${A} and ${L}.")
@annotation.inductive
trait CustomMappingEncoder[A, L <: HList] extends Encoder[A]

/**
  * Utilities for [[CustomMappingEncoder]]
  */
final object CustomMappingEncoder {

  def apply[A, L <: HList](implicit instance: CustomMappingEncoder[A, L]): CustomMappingEncoder[A, L] = instance

}
