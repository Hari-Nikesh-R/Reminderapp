package com.dosmartie.remainderapp

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class FutureDevelopment(
  val message: String = "",
)