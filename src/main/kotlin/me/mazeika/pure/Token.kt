package me.mazeika.pure

import kotlin.String as KString

/**
 * Represents a scanned token with a location at the offset from the beginning
 * of the source text.
 */
sealed class Token(val lexeme: KString, val offset: Int) {

    override fun toString(): KString = lexeme

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        javaClass != other?.javaClass -> false
        other is Token -> lexeme == other.lexeme
        else -> false
    }

    override fun hashCode(): Int {
        return lexeme.hashCode()
    }

    /**
     * Represents a literal token where the [type] is one of a string, number,
     * etc. and the [value] is the scanned value of that type.
     */
    abstract class Literal<out T>(
        private val type: KString,
        offset: Int,
        val value: T
    ) : Token(type, offset) {

        override fun toString(): KString = value.toString()

        override fun equals(other: Any?): Boolean = when {
            this === other -> true
            javaClass != other?.javaClass -> false
            !super.equals(other) -> false
            other is Literal<*> -> value == other.value
            else -> false
        }

        override fun hashCode(): Int =
            31 * super.hashCode() + (value?.hashCode() ?: 0)
    }

    class LeftParen(offset: Int) : Token("(", offset)

    class RightParen(offset: Int) : Token(")", offset)

    class LeftBrace(offset: Int) : Token("{", offset)

    class RightBrace(offset: Int) : Token("}", offset)

    class Comma(offset: Int) : Token(",", offset)

    class Dot(offset: Int) : Token(".", offset)

    class Minus(offset: Int) : Token("-", offset)

    class Plus(offset: Int) : Token("+", offset)

    class Semicolon(offset: Int) : Token(";", offset)

    class Slash(offset: Int) : Token("/", offset)

    class Star(offset: Int) : Token("*", offset)

    class Bang(offset: Int) : Token("!", offset)

    class BangEqual(offset: Int) : Token("!=", offset)

    class Equal(offset: Int) : Token("=", offset)

    class EqualEqual(offset: Int) : Token("==", offset)

    class Greater(offset: Int) : Token(">", offset)

    class GreaterEqual(offset: Int) : Token(">=", offset)

    class Less(offset: Int) : Token("<", offset)

    class LessEqual(offset: Int) : Token("<=", offset)

    class Identifier(offset: Int, value: KString) :
        Literal<KString>("identifier", offset, value)

    class String(offset: Int, value: KString) :
        Literal<KString>("string", offset, value) {

        override fun toString(): KString = "\"$value\""
    }

    class Number(offset: Int, value: Double) :
        Literal<Double>("number", offset, value)

    class And(offset: Int) : Token("and", offset)

    class Class(offset: Int) : Token("class", offset)

    class Else(offset: Int) : Token("else", offset)

    class False(offset: Int) : Token("false", offset)

    class Fun(offset: Int) : Token("fun", offset)

    class For(offset: Int) : Token("for", offset)

    class If(offset: Int) : Token("if", offset)

    class Nil(offset: Int) : Token("nil", offset)

    class Or(offset: Int) : Token("or", offset)

    class Print(offset: Int) : Token("print", offset)

    class Return(offset: Int) : Token("return", offset)

    class Super(offset: Int) : Token("super", offset)

    class This(offset: Int) : Token("this", offset)

    class True(offset: Int) : Token("true", offset)

    class Var(offset: Int) : Token("var", offset)

    class While(offset: Int) : Token("while", offset)

    class EOF(offset: Int) : Token("eof", offset)
}
