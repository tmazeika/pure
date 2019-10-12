package me.mazeika.pure.scan

import me.mazeika.pure.Token
import me.mazeika.pure.exception.PureException

/** Represents a source text scanner. */
interface Scanner {

    /**
     * Scans all tokens from [source] text.
     */
    fun scan(source: String): Sequence<Token>

    companion object {
        fun createDefault(onException: (PureException) -> Unit): Scanner = DefaultScanner(onException)
    }
}
