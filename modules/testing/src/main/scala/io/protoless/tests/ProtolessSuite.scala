package io.protoless.tests

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest._

trait ProtolessSuite extends FreeSpec with MustMatchers
  with TypeCheckedTripleEquals
  with EitherValues with OptionValues

