package io.protoless.core.tag

/**
  * Uses variable-length encoding for Unsigned values (uint32, uint64).
  */
sealed trait Unsigned

/**
  * Uses variable-length encoding. Signed int value.
  * These more efficiently encode negative numbers than regular int32/64s (sint32, sint64)
  */
sealed trait Signed

/**
  * Always four/height bytes.
  * More efficient than uint32/uint64 if values are often greater than 2^28/2^64 (fixed32, fixed64)
  */
sealed trait Fixed
