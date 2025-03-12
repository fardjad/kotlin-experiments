package com.fardjad.learning.errors

import com.fardjad.learning.errors.calculator.AverageUseCase
import com.fardjad.learning.errors.calculator.SumUseCase
import com.fardjad.learning.errors.common.AverageUseCaseZeroInputException
import com.fardjad.learning.errors.common.GeneratedException
import com.fardjad.learning.errors.common.SumUseCaseNegativeInputException
import com.fardjad.learning.errors.common.SumUseCaseNonIntInputException
import com.fardjad.learning.errors.common.SumUseCaseZeroInputException
import kotlin.system.exitProcess

private fun handleDomainExceptions(mapExitCodeFn: (e: GeneratedException) -> Int, block: () -> Unit) {
    try {
        block()
    } catch (e: GeneratedException) {
        println("Error: ${e.message}")
        exitProcess(mapExitCodeFn(e))
    }
}

fun main(vararg args: String) = handleDomainExceptions({ exception ->
    when (exception) {
        is SumUseCaseNegativeInputException -> 100
        is SumUseCaseNonIntInputException -> 101
        is SumUseCaseZeroInputException, is AverageUseCaseZeroInputException -> 102
    }
}) {
    if (args.size != 2) {
        println("Usage: generate-exception-example <a> <b>")
        exitProcess(1)
    }

    val a = args[0]
    val b = args[1]

    val sumOutput = SumUseCase().execute(
        SumUseCase.Input(a, b)
    )
    println("Sum of $a and $b is ${sumOutput.result}")

    val averageOutput = AverageUseCase().execute(
        AverageUseCase.Input(sumOutput.result, 2u)
    )
    println("Average is: ${averageOutput.result}")
}
