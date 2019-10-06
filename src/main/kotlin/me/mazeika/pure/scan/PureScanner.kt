package me.mazeika.pure.scan

import me.mazeika.pure.Token
import me.mazeika.pure.exception.PureException
import me.mazeika.pure.exception.ScanException

/** Represents a token scanner for Pure [source] text. */
class PureScanner(private val source: String) : Scanner {

    /** The allowed digits for numbers. */
    private val digits = '0'..'9'

    /** The allowed alphanumeric characters for identifiers. */
    private val alphanumeric = ('0'..'9') + ('a'..'z') + ('A'..'Z') + '_'

    override fun tokenize(
        onException: (e: PureException) -> Unit
    ): Sequence<Token> = Tokenizer(onException).tokenize()

    /**
     * Represents a tokenizer of Pure [source] text.
     *
     * Calls [onException] for every exception that occurs while tokenizing.
     */
    private inner class Tokenizer(
        private val onException: (e: PureException) -> Unit
    ) {
        private var startIdx = 0
        private var currentIdx = 0

        fun tokenize(): Sequence<Token> = sequence {
            while (!isAtEnd()) {
                startIdx = currentIdx

                when (val ch = advance()) {
                    '(' -> yield(Token.LeftParen(startIdx))
                    ')' -> yield(Token.RightParen(startIdx))
                    '{' -> yield(Token.LeftBrace(startIdx))
                    '}' -> yield(Token.RightBrace(startIdx))
                    ',' -> yield(Token.Comma(startIdx))
                    '.' -> yield(Token.Dot(startIdx))
                    '-' -> yield(Token.Minus(startIdx))
                    '+' -> yield(Token.Plus(startIdx))
                    ';' -> yield(Token.Semicolon(startIdx))
                    '*' -> yield(Token.Star(startIdx))
                    '!' -> yield(
                        if (match('=')) Token.BangEqual(startIdx)
                        else Token.Bang(startIdx)
                    )
                    '=' -> yield(
                        if (match('=')) Token.EqualEqual(startIdx)
                        else Token.Equal(startIdx)
                    )
                    '<' -> yield(
                        if (match('=')) Token.LessEqual(startIdx)
                        else Token.Less(startIdx)
                    )
                    '>' -> yield(
                        if (match('=')) Token.GreaterEqual(startIdx)
                        else Token.Greater(startIdx)
                    )
                    '/' -> {
                        if (match('/')) {
                            // consume entire comment line
                            while (peek1() != '\n' && !isAtEnd()) advance()
                        } else {
                            yield(Token.Slash(startIdx))
                        }
                    }
                    '"' -> {
                        // consume all string characters
                        while (peek1() != '"' && !isAtEnd()) advance()

                        if (isAtEnd()) {
                            onException(
                                ScanException(
                                    "Unterminated string",
                                    startIdx,
                                    currentIdx - startIdx
                                )
                            )
                        } else {
                            // consume the closing '"'
                            advance()

                            yield(
                                Token.String(
                                    startIdx, getLexeme().substring(
                                        1, currentIdx - startIdx - 1
                                    )
                                )
                            )
                        }
                    }
                    in digits -> {
                        // consume whole number
                        while (peek1() in digits) advance()

                        // look for fraction part
                        if (peek1() == '.' && peek2() in digits) {
                            // consume '.'
                            advance()

                            // consume fractional part
                            while (peek1() in digits) advance()
                        }

                        yield(
                            Token.Number(
                                startIdx, getLexeme().toDouble()
                            )
                        )
                    }
                    in alphanumeric -> {
                        // consume whole identifier
                        while (peek1() in alphanumeric) advance()

                        yield(
                            getReservedWord(getLexeme()) ?: Token.Identifier(
                                startIdx, getLexeme()
                            )
                        )
                    }
                    ' ', '\t', '\r', '\n' -> {
                        // ignore whitespace
                    }
                    else -> onException(
                        ScanException(
                            "Unexpected character: $ch", startIdx, 1
                        )
                    )
                }
            }

            yield(Token.EOF(++currentIdx))
        }

        /** Gets the current lexeme scanned so far. */
        private fun getLexeme(): String = source.substring(startIdx, currentIdx)

        /** Gets whether the current index is at the end of the source text. */
        private fun isAtEnd(): Boolean = (currentIdx == source.length)

        /**
         * Consumes one character.
         *
         * Returns the character at the current index and then increments the
         * current index.
         */
        private fun advance(): Char = source[currentIdx++]

        /**
         * Conditionally consumes one character.
         *
         * Checks whether the character at the current index is equal to the
         * [expected] character. If so, and the current index is not at the end
         * of the source text, then the current index is incremented and `true`
         * is returned.
         */
        private fun match(expected: Char): Boolean = when {
            isAtEnd() || source[currentIdx] != expected -> false
            else -> {
                currentIdx++
                true
            }
        }

        /**
         * Looks ahead one character.
         *
         * Returns `null` when the current index is at the end of the source
         * text.
         */
        private fun peek1(): Char? = if (isAtEnd()) null else source[currentIdx]

        /**
         * Looks ahead two characters.
         *
         * Returns `null` when the next index from the current index is at the
         * end of the source text.
         */
        private fun peek2(): Char? = if (currentIdx + 1 >= source.length) {
            null
        } else {
            source[currentIdx + 1]
        }

        /**
         * Gets the [Token] associated with the given [lexeme] or `null` if
         * [lexeme] is not a reserved word.
         */
        private fun getReservedWord(lexeme: String): Token? = when (lexeme) {
            "and" -> Token.And(startIdx)
            "class" -> Token.Class(startIdx)
            "else" -> Token.Else(startIdx)
            "false" -> Token.False(startIdx)
            "for" -> Token.For(startIdx)
            "fun" -> Token.Fun(startIdx)
            "if" -> Token.If(startIdx)
            "nil" -> Token.Nil(startIdx)
            "or" -> Token.Or(startIdx)
            "print" -> Token.Print(startIdx)
            "return" -> Token.Return(startIdx)
            "super" -> Token.Super(startIdx)
            "this" -> Token.This(startIdx)
            "true" -> Token.True(startIdx)
            "var" -> Token.Var(startIdx)
            "while" -> Token.While(startIdx)
            else -> null
        }
    }
}
