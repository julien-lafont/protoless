package io.protoless.generic.encoding

import io.protoless.messages.encoders.CustomMappingEncoder
import shapeless.HList

abstract class DerivedCustomMappingEncoder[A, L <: HList] extends CustomMappingEncoder[A, L]

final object DerivedCustomMappingEncoder extends CustomMappingEncoderInstances