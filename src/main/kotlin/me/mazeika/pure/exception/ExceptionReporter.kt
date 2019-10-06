package me.mazeika.pure.exception

/** Represents a reporter of compile time exceptions. */
interface ExceptionReporter {

    /** Reports the given exception [e]. */
    fun report(e: PureException)
}
