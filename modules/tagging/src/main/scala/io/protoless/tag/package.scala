package io.protoless

import shapeless.tag.{Tagged, Tagger}

import io.protoless.tag.TagRestriction.NumericTagRestriction

/**
  * Use Shapeless tags to refine int/long values as [[tag.Signed]], [[tag.Unsigned]] or [[tag.Fixed]].
  */
package object tag {

  /**
    * Shadow shapeless tag type
    */
  type @@[+T, U] = T with Tagged[U]

  /**
    * Tag a numeric field with a `Unsigned` tag
    */
  def unsigned[T: NumericTagRestriction](t: T): T @@ Unsigned = new Tagger[Unsigned].apply[T](t)

  /**
    * Tag a numeric field with a `Signed` tag
    */
  def signed[T: NumericTagRestriction](t: T): T @@ Signed = new Tagger[Signed].apply[T](t)

  /**
    * Tag a numeric field with a `Fixed` tag
    */
  def fixed[T: NumericTagRestriction](t: T): T @@ Fixed = new Tagger[Fixed].apply[T](t)

  /**
    * Tag a numeric field with a `Signed` and `Fixed` tag
    */
  def signedFixed[T: NumericTagRestriction](t: T): T @@ Signed with Fixed = new Tagger[Signed with Fixed].apply[T](t)
}
