package me.mazeika.pure.exception

/** Represents a reporter of Pure exceptions. */
interface ExceptionReporter {

    /** Reports that [e] has occurred. */
    fun report(e: PureException)
}
