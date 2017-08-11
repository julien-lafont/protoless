package io.protoless.generic.decoding.internal

import shapeless.Nat

import io.protoless.Decoder
import io.protoless.decoders.IncrementalDecoder
import io.protoless.generic.decoding.IncrementalDecoderInstances

/**
  * Internal class allowing to restrict automatic derivation of type `A`.
  *
  * [[SemiAutoDecoder]] can only be retrieved with `io.protoless.semiauto.deriveDecoder`.
  */
private[protoless] class SemiAutoDecoder[A](val underlying: Decoder[A])

private[protoless] trait SemiAutoDecoderInstances extends IncrementalDecoderInstances {

  implicit def decodeSemiAutoInstance[A](implicit decoder: IncrementalDecoder[A, Nat._1]): SemiAutoDecoder[A] = {
    new SemiAutoDecoder[A](decoder)
  }

}
