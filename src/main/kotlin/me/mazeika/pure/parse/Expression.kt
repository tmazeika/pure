package me.mazeika.pure.parse

import me.mazeika.pure.Token

/** Represents an expression that can be evaluated. */
sealed class Expression {

    data class Assign(val name: Token, val value: Expression) : Expression()

    data class Binary(val left: Expression, val op: Token, val right: Expression) : Expression()

    data class Call(val callee: Expression, val paren: Token, val args: List<Expression>) : Expression()

    data class Grouping(val expr: Expression) : Expression()

    data class Literal(val value: Any?) : Expression() {

        override fun toString(): String = when (this.value) {
            is String -> "\"$value\""
            null -> "nil"
            else -> this.value.toString()
        }
    }

    data class Logical(val left: Expression, val op: Token, val right: Expression) : Expression()

    data class Unary(val op: Token, val right: Expression) : Expression()

    data class Variable(val name: Token) : Expression() {

        override fun toString(): String = this.name.toString()
    }
}




