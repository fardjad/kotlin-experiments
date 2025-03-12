package com.fardjad.learning.ksp

import java.lang.RuntimeException
import kotlin.Boolean
import kotlin.String
import kotlin.Throwable

public sealed class GeneratedException: RuntimeException {
    protected constructor() : super()

    protected constructor(message: String) : super(message)

    protected constructor(message: String, cause: Throwable) : super(message, cause)

    protected constructor(cause: Throwable) : super(cause)

    protected constructor(
        message: String,
        cause: Throwable,
        enableSuppression: Boolean,
        writableStackTrace: Boolean,
    ) : super(message, cause, enableSuppression, writableStackTrace)
}

public class TestException : GeneratedException {
    public constructor() : super()

    public constructor(message: String) : super(message)

    public constructor(message: String, cause: Throwable) : super(message, cause)

    public constructor(cause: Throwable) : super(cause)

    public constructor(
        message: String,
        cause: Throwable,
        enableSuppression: Boolean,
        writableStackTrace: Boolean,
    ) : super(message, cause, enableSuppression, writableStackTrace)
}
