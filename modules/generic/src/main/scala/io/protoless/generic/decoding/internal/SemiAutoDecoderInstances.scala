package io.protoless.generic.decoding.internal

import shapeless.Nat
import io.protoless.generic.decoding.{CustomMappingDecoderInstances, IncrementalDecoderInstances}
import io.protoless.messages.Decoder
import io.protoless.messages.decoders.IncrementalDecoder

/**
  * Internal class allowing to restrict automatic derivation of type `A`.
  *
  * [[SemiAutoDecoder]] can only be retrieved with `io.protoless.generic.semiauto.deriveDecoder`.
  */
private[protoless] class SemiAutoDecoder[A](val underlying: Decoder[A])

private[protoless] trait SemiAutoDecoderInstances extends CustomMappingDecoderInstances {

  implicit def decodeSemiAutoInstance[A](implicit decoder: IncrementalDecoder[A, Nat._1]): SemiAutoDecoder[A] = {
    new SemiAutoDecoder[A](decoder)
  }

}
