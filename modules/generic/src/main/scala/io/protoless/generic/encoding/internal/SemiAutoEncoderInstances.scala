package io.protoless.generic.encoding.internal

import shapeless.Nat
import io.protoless.generic.encoding.DerivedIncrementalEncoder
import io.protoless.messages.Encoder

/**
  * Internal class allowing to restrict automatic derivation of type `A`.
  *
  * [[SemiAutoEncoder]] can only be retrieved with `io.protoless.generic.semiauto.deriveEncoder`.
  */
private[protoless] class SemiAutoEncoder[A](val underlying: Encoder[A])

private[protoless] trait SemiAutoEncoderInstances {

  implicit def encodeSemiAutoInstance[A](implicit encoder: DerivedIncrementalEncoder[A, Nat._1]): SemiAutoEncoder[A] = {
    new SemiAutoEncoder[A](encoder)
  }
}

object SemiAutoEncoder extends SemiAutoEncoderInstances
