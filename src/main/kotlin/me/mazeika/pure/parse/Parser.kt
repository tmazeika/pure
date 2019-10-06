package me.mazeika.pure.parse

import me.mazeika.pure.exception.PureException

/** Represents a token parser. */
interface Parser {

    /**
     * Converts the tokens into an expression.
     *
     * // TODO
     */
    fun parse(onException: (e: PureException) -> Unit): Expr?
}
