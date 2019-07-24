package io.protoless.messages.encoders

import scala.annotation.implicitNotFound

import shapeless.HList

import io.protoless.messages.Encoder

@implicitNotFound("No CustomMappingEncoder found for type ${A} and ${L}.")
trait CustomMappingEncoder[A, L <: HList] extends Encoder[A]

/**
  * Utilities for [[CustomMappingEncoder]]
  */
final object CustomMappingEncoder {

  def apply[A, L <: HList](implicit instance: CustomMappingEncoder[A, L]): CustomMappingEncoder[A, L] = instance

}
