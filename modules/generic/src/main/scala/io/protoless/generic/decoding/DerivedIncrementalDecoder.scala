package io.protoless.generic.decoding

import io.protoless.messages.decoders.IncrementalDecoder
import shapeless.Nat

abstract class DerivedIncrementalDecoder[A, N <: Nat] extends IncrementalDecoder[A, N]

object DerivedIncrementalDecoder extends IncrementalDecoderInstances
