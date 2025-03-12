package com.fardjad.learning.errors.common

abstract class UseCase<in InputType, out OutputType> where OutputType : Any {
    abstract fun execute(input: InputType): OutputType
}