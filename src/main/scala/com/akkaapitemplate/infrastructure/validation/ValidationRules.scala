package com.akkaapitemplate.infrastructure.validation

import com.osinka.i18n.{Lang, Messages}
import RejectionCategory._

trait ValidationRules {

  implicit val userLang = Lang("pt")

  def notEmpty[T](value: String, target: String): (T) => (Boolean, Rejection) = {
    obj => (value.trim.nonEmpty, Rejection(VALIDATION, target, Messages("validation.required", Messages(target)), "validation.required", List(target)))
  }

  def ensure[T](condition: Boolean, target: String, i18nKey: String): (T) => (Boolean, Rejection) = {
    obj => (condition, Rejection(VALIDATION, target, Messages(i18nKey), i18nKey))
  }

  def between[T](value: Double, min: Double, max: Double, target: String): (T) => (Boolean, Rejection) = {
    obj => (value >= min && value <= max, Rejection(VALIDATION, target, Messages("validation.between", Messages(target), min, max), "validation.between", List(target, min, max)))
  }

}
