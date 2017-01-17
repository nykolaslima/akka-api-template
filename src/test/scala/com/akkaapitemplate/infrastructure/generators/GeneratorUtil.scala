package com.akkaapitemplate.infrastructure.generators

import org.scalacheck.Gen

trait GeneratorUtil {
  def alphaStr = (n: Int) => Gen.listOfN(n, Gen.alphaChar).map(_.mkString)
  def numStr = (n: Int) => Gen.listOfN(n, Gen.numChar).map(_.mkString)
  def some[T](g: Gen[T]): Gen[Option[T]] = g.map(Some.apply)
}
