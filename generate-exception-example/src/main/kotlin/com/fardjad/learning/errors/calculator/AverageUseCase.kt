package com.fardjad.learning.errors.calculator

import com.fardjad.learning.errors.common.AverageUseCaseZeroInputException
import com.fardjad.learning.errors.common.SumUseCaseNegativeInputException
import com.fardjad.learning.errors.common.SumUseCaseZeroInputException
import com.fardjad.learning.errors.common.UseCase
import com.fardjad.learning.ksp.GenerateExceptionClass

@GenerateExceptionClass("ZeroInputException")
class AverageUseCase : UseCase<AverageUseCase.Input, AverageUseCase.Output>() {
    override fun execute(input: Input): Output {
        if (input.count == 0u) {
            throw AverageUseCaseZeroInputException("Count cannot be zero")
        }

        return Output(input.sum.toDouble() / input.count.toDouble())
    }

    data class Input(val sum: Int, val count: UInt)
    data class Output(val result: Double)
}
