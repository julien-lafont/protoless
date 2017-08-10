package io.protoless.generic.encoding.internal

import shapeless.Nat

import io.protoless.core.Encoder
import io.protoless.core.encoders.IncrementalEncoder
import io.protoless.generic.encoding.IncrementalEncoderInstances

private[protoless] class SemiAutoEncoder[A](val underlying: Encoder[A])

private[protoless] class SemiAutoEncoderInstances extends IncrementalEncoderInstances {

  implicit def encodeSemiAutoInstance[A](implicit encoder: IncrementalEncoder[A, Nat._1]): SemiAutoEncoder[A] = {
    new SemiAutoEncoder[A](encoder)
  }
}
