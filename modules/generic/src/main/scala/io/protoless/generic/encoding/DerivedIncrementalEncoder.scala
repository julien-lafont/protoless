package io.protoless.generic.encoding

import io.protoless.messages.encoders.IncrementalEncoder
import shapeless.Nat

abstract class DerivedIncrementalEncoder[A, N <: Nat] extends IncrementalEncoder[A, N]

object DerivedIncrementalEncoder extends IncrementalEncoderInstances
