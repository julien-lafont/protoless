package io.protoless.generic.decoding

import io.protoless.messages.decoders.CustomMappingDecoder
import shapeless.HList

abstract class DerivedCustomMappingDecoder[A, L <: HList] extends CustomMappingDecoder[A, L]

final object DerivedCustomMappingDecoder extends CustomMappingDecoderInstances