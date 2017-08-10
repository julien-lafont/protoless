package io.protoless.tests.instances

import java.util.UUID

import com.google.protobuf.ByteString
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

import io.protoless.core.tag._

trait ArbitraryInstances {

  implicit val arbitraryByteString: Arbitrary[ByteString] = Arbitrary(Arbitrary.arbString.arbitrary.map(ByteString.copyFromUtf8))

  implicit val arbitraryUInt: Arbitrary[Int @@ Unsigned] = Arbitrary(Gen.posNum[Int].map(i => unsigned(i)))
  implicit val arbitraryULong: Arbitrary[Long @@ Unsigned] = Arbitrary(Gen.posNum[Long].map(i => unsigned(i)))

  implicit val arbitrarySInt: Arbitrary[Int @@ Signed] = Arbitrary(arbitrary[Int].map(i => signed(i)))
  implicit val arbitrarySLong: Arbitrary[Long @@ Signed] = Arbitrary(arbitrary[Long].map(i => signed(i)))

  implicit val arbitraryFInt: Arbitrary[Int @@ Fixed] = Arbitrary(arbitrary[Int].map(i => fixed(i)))
  implicit val arbitraryFLong: Arbitrary[Long @@ Fixed] = Arbitrary(arbitrary[Long].map(i => fixed(i)))

  implicit val arbitraryFSInt: Arbitrary[Int @@ Signed with Fixed] = Arbitrary(arbitrary[Int].map(i => signedFixed(i)))
  implicit val arbitraryFSLong: Arbitrary[Long @@ Signed with Fixed] = Arbitrary(arbitrary[Long].map(i => signedFixed(i)))

  implicit val arbitraryUUID: Arbitrary[UUID] = Arbitrary(Gen.uuid)

}
