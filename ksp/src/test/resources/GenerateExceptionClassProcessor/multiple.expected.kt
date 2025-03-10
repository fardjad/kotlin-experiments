package com.fardjad.learning.ksp

import java.lang.RuntimeException
import kotlin.Boolean
import kotlin.String
import kotlin.Throwable

public sealed interface GeneratedException

public class TestException1 : RuntimeException, GeneratedException {
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

public class TestException2 : RuntimeException, GeneratedException {
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
