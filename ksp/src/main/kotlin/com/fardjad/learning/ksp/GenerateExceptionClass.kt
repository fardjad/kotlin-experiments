package com.fardjad.learning.ksp

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@Repeatable
annotation class GenerateExceptionClass(val name: String)
