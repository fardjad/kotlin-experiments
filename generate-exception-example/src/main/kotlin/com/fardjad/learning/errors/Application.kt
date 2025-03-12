package com.fardjad.learning.errors

import com.fardjad.learning.errors.calculator.SumUseCase
import com.fardjad.learning.errors.common.GeneratedException
import com.fardjad.learning.errors.common.NegativeInputException
import com.fardjad.learning.errors.common.NonIntInputException
import com.fardjad.learning.errors.common.ZeroInputException
import kotlin.system.exitProcess

fun runCommandLine(mapExitCodeFn: (e: GeneratedException) -> Int, block: () -> Unit) {
    class CommandLineException(cause: Throwable, val exitCode: Int) : RuntimeException(cause) {
        fun die() {
            println("Error: ${cause?.message}")
            exitProcess(exitCode)
        }
    }

    try {
        block()
    } catch (e: GeneratedException) {
        CommandLineException(e, mapExitCodeFn(e)).die()
    }
}

fun main(vararg args: String) = runCommandLine({ exception ->
    when (exception) {
        is NegativeInputException -> 100
        is NonIntInputException -> 101
        is ZeroInputException -> 102
    }
}) {
    if (args.size != 2) {
        println("Usage: generate-exception-example <a> <b>")
        exitProcess(1)
    }

    val a = args[0]
    val b = args[1]

    val output = SumUseCase().execute(
        SumUseCase.Input(a, b)
    )

    println("Sum of $a and $b is ${output.result}")
}
