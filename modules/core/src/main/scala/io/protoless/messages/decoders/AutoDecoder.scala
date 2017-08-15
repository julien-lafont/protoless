package io.protoless.decoders

import scala.annotation.implicitNotFound

import io.protoless.Decoder

/**
  * A type class that decode a value of type `A` from a `CodedInputSteam`.
  * Each parameters of `A` is associated to an incremental index (starting from 1) representing the protobuf field index.
  *
  * The decoder is implicitly transformed into an [[IncrementalDecoder]][A, Nat._1] after the first induction step.
  */
@implicitNotFound("No AutoDecoder found for type ${A}.")
@annotation.inductive
trait AutoDecoder[A] extends Decoder[A]

/**
  * Utilities for [[AutoDecoder]]
  */
final object AutoDecoder {

  def apply[A](implicit instance: AutoDecoder[A]): AutoDecoder[A] = instance

}
