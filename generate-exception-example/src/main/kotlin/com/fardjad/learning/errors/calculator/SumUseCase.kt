package com.fardjad.learning.errors.calculator

import com.fardjad.learning.errors.common.NegativeInputException
import com.fardjad.learning.errors.common.NonIntInputException
import com.fardjad.learning.errors.common.UseCase
import com.fardjad.learning.errors.common.ZeroInputException
import com.fardjad.learning.ksp.GenerateExceptionClass

@GenerateExceptionClass("NegativeInputException")
@GenerateExceptionClass("ZeroInputException")
@GenerateExceptionClass("NonIntInputException")
class SumUseCase : UseCase<SumUseCase.Input, SumUseCase.Output>() {
    override fun execute(input: Input): Output {
        val aNum: Int
        val bNum: Int

        try {
            aNum = input.a.toInt()
            bNum = input.b.toInt()
        } catch (_: NonIntInputException) {
            throw NonIntInputException("Input values must be integers")
        }

        if (aNum < 0 || bNum < 0) {
            throw NegativeInputException("Input values must be positive")
        }

        if (aNum == 0 || bNum == 0) {
            throw ZeroInputException("Input values must not be zero")
        }

        return Output(aNum + bNum)
    }

    data class Input(val a: String, val b: String)
    data class Output(val result: Int)
}
