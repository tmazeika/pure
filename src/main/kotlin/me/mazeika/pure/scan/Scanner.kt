package me.mazeika.pure.scan

import me.mazeika.pure.Token
import me.mazeika.pure.exception.PureException

/** Represents a token scanner for source text. */
interface Scanner {

    /**
     * Scans all tokens from a source text.
     *
     * Calls [onException] when a scanning exception occurs. [onException]
     * may be called zero or more times, but tokens will always continue to
     * be scanned as normal after it has been called. One or more calls to
     * [onException] indicates that the program should not be executed.
     */
    fun tokenize(onException: (e: PureException) -> Unit): Sequence<Token>
}
