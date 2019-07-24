package io.protoless

import shapeless.HList

import io.protoless.messages.decoders.CustomMappingDecoder
import io.protoless.messages.encoders.CustomMappingEncoder
import io.protoless.generic.decoding.{AutoDecoderInstances, CustomMappingDecoderInstances}
import io.protoless.generic.decoding.internal.{SemiAutoDecoder, SemiAutoDecoderInstances}
import io.protoless.generic.encoding.{AutoEncoderInstances, CustomMappingEncoderInstances}
import io.protoless.generic.encoding.internal.{SemiAutoEncoder, SemiAutoEncoderInstances}
import io.protoless.messages.{Decoder, Encoder}

package object generic {

  /**
    * Allows to automatically import required [[Decoder]] and [[Encoder]] in the scope.
    *
    * Only [[messages.decoders.AutoDecoder]] and [[messages.encoders.AutoEncoder]] can be derived automatically.
    *
    * You can still derive [[messages.decoders.CustomMappingDecoder]] and [[messages.encoders.CustomMappingEncoder]]
    * with `semiauto.deriveDecoder[A, L]` or by summoning a decoder with `CustomMappingDecoder[A, HList]` (idem for Encoders).
    */
  object auto extends AutoDecoderInstances with AutoEncoderInstances 

  /**
    * Allows to manually derive [[Decoder]] and [[Encoder]],
    * either with `Automatic` strategy or `Custom Mapping` strategy.
    */
  object semiauto extends SemiAutoEncoderInstances with SemiAutoDecoderInstances with CustomMappingDecoderInstances with CustomMappingEncoderInstances {

    /**
      * Derive an [[Decoder]] to decode a type `A` from a protobuf message, for which
      * each parameter is associated to a protobuf field using an `Automatic` indexing.
      *
      * ==Automatic Indexing==
      * Each parameter of product `A` is associated with a protobuf field starting from 1 to Length[Gen[A]].
      *  - parameter 1: Field index 1
      *  - parameter 2: Field index 2
      *
      * For example, this case class matches the following protobuf message:
      *
      * {{{
      * case class Person(firstname: String, lastname: String, age: Option[Int], locations: Seq[String])
      * }}}
      *
      * {{{
      * message MyPerson {
      *   string name1 = 1;
      *   string name2 = 2;
      *   int32 age = 3;
      *   repeated string locations = 4;
      * }
      * }}}
      *
      * @tparam A Result of the decoder
      */
    def deriveDecoder[A](implicit decoder: SemiAutoDecoder[A]): Decoder[A] = decoder.underlying

    /**
      * Derive an [[Decoder]] to decode a type `A` from a protobuf message, for which
      * you can customize the protobuf field number of each `A` parameters.
      *
      * The expected `L` HList must contains only `Nat`, and must have as many elements as the `A` has parameters.
      *
      * ==Custom Mapping==
      * Each parameter of product `A` is associated with the Nth element of the HList `L` .
      *  - parameter 1: `Nat` as position `0` in `L`
      *  - parameter 2: `Nat` as position `1` in `L`
      *
      * For example, this case class, with custom mapping `Nat._2, Nat._4, Nat._5, Nat._7` matches the following protobuf message:
      *
      * {{{
      * case class Person(firstname: String, lastname: String, age: Option[Int], locations: Seq[String])
      * }}}
      *
      * {{{
      * message MyPerson {
      *   int32 id = 1
      *   string name1 = 2;
      *   // deprecated field: string middlename = 3;
      *   string name2 = 4
      *   int32 age = 5;
      *   string phone = 6;
      *   repeated string locations = 7;
      * }
      * }}}
      *
      * @tparam A Result of the decoder
      * @tparam L Mapping from `A` parameters to Protobuf field index
      */
    def deriveDecoder[A, L <: HList](implicit decoder: CustomMappingDecoder[A, L]): Decoder[A] = decoder

    /**
      * Derive an [[Encoder]] to encode a type `A` in a protobuf message, for which
      * each parameter is associated to a protobuf field using an `Automatic` indexing.
      *
      * ==Automatic Indexing==
      * Each parameter of product `A` is associated with a protobuf field starting from 1 to Length[Gen[A]].
      *  - parameter 1: Field index 1
      *  - parameter 2: Field index 2
      *
      * For example, this case class matches the following protobuf message:
      *
      * {{{
      * case class Person(firstname: String, lastname: String, age: Option[Int], locations: Seq[String])
      * }}}
      *
      * {{{
      * message MyPerson {
      *   string name1 = 1;
      *   string name2 = 2;
      *   int32 age = 3;
      *   repeated string locations = 4;
      * }
      * }}}
      *
      * @tparam A Type to encode
      */
    def deriveEncoder[A](implicit encoder: SemiAutoEncoder[A]): Encoder[A] = encoder.underlying

    /**
      * Derive an [[Encoder]] to encode a type `A` in a protobuf message, for which
      * you can customize the protobuf field number of each `A` parameters.
      *
      * The expected `L` HList must contains only `Nat`, and must have as many elements as the `A` has parameters.
      *
      * ==Custom Mapping==
      * Each parameter of product `A` is associated with the Nth element of the HList `L` .
      *  - parameter 1: `Nat` as position `0` in `L`
      *  - parameter 2: `Nat` as position `1` in `L`
      *
      * For example, this case class, with custom mapping `Nat._2, Nat._4, Nat._5, Nat._7` matches the following protobuf message:
      *
      * {{{
      * case class Person(firstname: String, lastname: String, age: Option[Int], locations: Seq[String])
      * }}}
      *
      * {{{
      * message MyPerson {
      *   int32 id = 1
      *   string name1 = 2;
      *   // deprecated field: string middlename = 3;
      *   string name2 = 4
      *   int32 age = 5;
      *   string phone = 6;
      *   repeated string locations = 7;
      * }
      * }}}
      *
      * @tparam A Type to encode
      * @tparam L Mapping from `A` parameters to Protobuf field index
      */
    def deriveEncoder[A, L <: HList](implicit encoder: CustomMappingEncoder[A, L]): Encoder[A] = encoder
  }
}
