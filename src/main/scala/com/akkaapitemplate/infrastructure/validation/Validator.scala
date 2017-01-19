package com.akkaapitemplate.infrastructure.validation

import algebra.Semigroup
import cats.data.Validated._
import cats.data.ValidatedNel
import cats.implicits._
import com.akkaapitemplate.infrastructure.validation.RejectionCategory.RejectionCategory

object Validator {
  def validate[T](value: T, rules: List[Rule[T]]): List[Rejection] = {
    implicit val semigroup = new Semigroup[T] {
      override def combine(x: T, y: T): T = x
    }

    def executeRule(rule: Rule[T]): ValidatedNel[Rejection, T] = rule.validate(value) match {
      case (condition, message) => if (condition) valid(value) else invalidNel(message)
    }

    val result = rules match {
      case x :: xs => xs.foldLeft(executeRule(x)) { case (z, n) => z.combine(executeRule(n)) }
      case Nil => valid(value)
    }

    result match {
      case Valid(v) => List()
      case Invalid(rejections) => rejections.unwrap
    }
  }
}

case class Rejection(category: RejectionCategory, target: String, message: String, key: String, args: List[Any] = List()) {
  def identifier = List(category.toString, target, message).mkString(".")
}

case class Rule[T](validate: T => (Boolean, Rejection))
