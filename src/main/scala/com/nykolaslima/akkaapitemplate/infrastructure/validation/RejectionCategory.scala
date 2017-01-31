package com.nykolaslima.akkaapitemplate.infrastructure.validation

object RejectionCategory extends Enumeration {
  type RejectionCategory = Value
  val VALIDATION = Value("VALIDATION")
  val INTERNAL = Value("INTERNAL")
}
