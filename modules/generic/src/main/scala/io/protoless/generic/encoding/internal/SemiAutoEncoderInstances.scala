package io.protoless.generic.encoding.internal

import shapeless.Nat

import io.protoless.generic.encoding.IncrementalEncoderInstances
import io.protoless.messages.Encoder
import io.protoless.messages.encoders.IncrementalEncoder

/**
  * Internal class allowing to restrict automatic derivation of type `A`.
  *
  * [[SemiAutoEncoder]] can only be retrieved with `io.protoless.generic.semiauto.deriveEncoder`.
  */
private[protoless] class SemiAutoEncoder[A](val underlying: Encoder[A])

private[protoless] class SemiAutoEncoderInstances extends IncrementalEncoderInstances {

  implicit def encodeSemiAutoInstance[A](implicit encoder: IncrementalEncoder[A, Nat._1]): SemiAutoEncoder[A] = {
    new SemiAutoEncoder[A](encoder)
  }
}
