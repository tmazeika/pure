package me.mazeika.pure.parse

import me.mazeika.pure.Token
import me.mazeika.pure.exception.PureException

/** Represents a token parser. */
interface Parser {

    /** Parses all [tokens] into statements. */
    fun parse(tokens: List<Token>): Sequence<Statement>

    companion object {
        fun createDefault(onException: (PureException) -> Unit): Parser = DefaultParser(onException)
    }
}
