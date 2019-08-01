package io.protoless.generic.decoding.internal

import shapeless.Nat
import io.protoless.generic.decoding.DerivedIncrementalDecoder
import io.protoless.messages.Decoder

/**
  * Internal class allowing to restrict automatic derivation of type `A`.
  *
  * [[SemiAutoDecoder]] can only be retrieved with `io.protoless.generic.semiauto.deriveDecoder`.
  */
private[protoless] class SemiAutoDecoder[A](val underlying: Decoder[A])

private[protoless] trait SemiAutoDecoderInstances {

  implicit def decodeSemiAutoIncrementalInstance[A](implicit decoder: DerivedIncrementalDecoder[A, Nat._1]): SemiAutoDecoder[A] = {
    new SemiAutoDecoder[A](decoder)
  }

}

object SemiAutoDecoder extends SemiAutoDecoderInstances
