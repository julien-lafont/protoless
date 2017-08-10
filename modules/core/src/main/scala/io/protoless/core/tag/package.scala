package io.protoless.core

import shapeless.tag.{Tagged, Tagger}

/**
  * Use Shapeless tags to refine int/long values as [[Signed]], [[Unsigned]] or [[Fixed]]..
  */
package object tag {

  /**
    * Shadow shapeless tag type
    */
  type @@[+T, U] = T with Tagged[U]

  //FIXME The `_ <: Numeric` evidence is to open according to protobuf field. Only Int and Long should be accepted.

  /**
    * Tag a numeric field with a `Unsigned` tag
    */
  def unsigned[T, _ <: Numeric[T]](t: T): T @@ Unsigned = new Tagger[Unsigned].apply[T](t)

  /**
    * Tag a numeric field with a `Signed` tag
    */
  def signed[T, _ <: Numeric[T]](t: T): T @@ Signed = new Tagger[Signed].apply[T](t)

  /**
    * Tag a numeric field with a `Fixed` tag
    */
  def fixed[T, _ <: Numeric[T]](t: T): T @@ Fixed = new Tagger[Fixed].apply[T](t)

  /**
    * Tag a numeric field with a `Signed` and `Fixed` tag
    */
  def signedFixed[T, _ <: Numeric[T]](t: T): T @@ Signed with Fixed = new Tagger[Signed with Fixed].apply[T](t)
}
