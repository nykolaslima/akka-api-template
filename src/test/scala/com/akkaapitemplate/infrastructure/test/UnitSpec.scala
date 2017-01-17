package com.akkaapitemplate.infrastructure.test

import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Matchers, WordSpecLike}

trait UnitSpec extends WordSpecLike with Matchers with BeforeAndAfter with BeforeAndAfterAll with MockFactory
