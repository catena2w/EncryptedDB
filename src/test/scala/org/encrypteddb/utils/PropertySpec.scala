package org.encrypteddb.utils

import org.scalacheck.Shrink
import org.scalatest.{Matchers, PropSpec}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

trait PropertySpec extends PropSpec with ScalaCheckDrivenPropertyChecks with Matchers with TestUtils {
  implicit def noShrink[A]: Shrink[A] = Shrink(_ => Stream.empty)

}
