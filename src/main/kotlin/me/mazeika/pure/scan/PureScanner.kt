package me.mazeika.pure.scan

import me.mazeika.pure.Token
import me.mazeika.pure.exception.CompilationException

object PureScanner : Scanner {

    /** The allowable digits for numbers. */
    private val digits = '0'..'9'

    /** The allowed alphanumeric characters for identifiers. */
    private val alphanumeric = ('0'..'9') + ('a'..'z') + ('A'..'Z') + '_'

    override fun scanTokens(
        source: String, onException: (e: CompilationException) -> Unit
    ): Sequence<Token> = sequence {
        var startIdx = 0
        var currentIdx = 0

        /** Gets the current lexeme scanned so far. */
        fun getLexeme(): String = source.substring(startIdx, currentIdx)

        /** Gets whether the current index is at the end of the source text. */
        fun isAtEnd(): Boolean = (currentIdx == source.length)

        /**
         * Consumes one character.
         *
         * Returns the character at the current index and then increments the
         * current index.
         */
        fun advance(): Char = source[currentIdx++]

        /**
         * Conditionally consumes one character.
         *
         * Checks whether the character at the current index is equal to the
         * [expected] character. If so, and the current index is not at the end
         * of the source text, then the current index is incremented and `true`
         * is returned.
         */
        fun match(expected: Char): Boolean = when {
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
        fun peek(): Char? = if (isAtEnd()) null else source[currentIdx]

        /**
         * Looks ahead two characters.
         *
         * Returns `null` when the next index from the current index is at the
         * end of the source text.
         */
        fun peek2(): Char? = if (currentIdx + 1 >= source.length) {
            null
        } else {
            source[currentIdx + 1]
        }

        /**
         * Gets the [Token] associated with the given [lexeme] or `null` if
         * [lexeme] is not a reserved word.
         */
        fun getReservedWord(lexeme: String): Token? = when (lexeme) {
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
                        while (peek() != '\n' && !isAtEnd()) advance()
                    } else {
                        yield(Token.Slash(startIdx))
                    }
                }
                '"' -> {
                    // consume all string characters
                    while (peek() != '"' && !isAtEnd()) advance()

                    if (isAtEnd()) {
                        onException(
                            CompilationException(
                                "Unterminated string",
                                source,
                                startIdx,
                                currentIdx - startIdx
                            )
                        )
                    } else {
                        // consume the closing '"'
                        advance()

                        yield(
                            Token.String(
                                startIdx,
                                getLexeme().substring(
                                    1,
                                    currentIdx - startIdx - 1
                                )
                            )
                        )
                    }
                }
                in digits -> {
                    // consume whole number
                    while (peek() in digits) advance()

                    // look for fraction part
                    if (peek() == '.' && peek2() in digits) {
                        // consume '.'
                        advance()

                        // consume fractional part
                        while (peek() in digits) advance()
                    }

                    yield(Token.Number(startIdx, getLexeme().toDouble()))
                }
                in alphanumeric -> {
                    // consume whole identifier
                    while (peek() in alphanumeric) advance()

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
                    CompilationException(
                        "Unexpected character '$ch'", source, startIdx, 1
                    )
                )
            }
        }

        yield(Token.EOF(currentIdx))
    }
}
