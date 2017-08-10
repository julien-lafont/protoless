package io.protoless.core.tag

private[protoless] object TagRestriction {

  /**
    * Explicitly allows primitive types to be tagged
    */
  sealed class NumericTagRestriction[V]

  object NumericTagRestriction {
    implicit val intTagRestriction: NumericTagRestriction[Int] = new NumericTagRestriction[Int]
    implicit val longTagRestriction: NumericTagRestriction[Long] = new NumericTagRestriction[Long]
  }
}
