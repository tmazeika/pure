package me.mazeika.pure.scan

import me.mazeika.pure.Token
import me.mazeika.pure.exception.PureException
import me.mazeika.pure.exception.ScanException

/** Represents a default source text scanner. */
internal class DefaultScanner(private val onException: (PureException) -> Unit) : Scanner {

    /** The allowed alphanumeric characters for identifiers. */
    private val alphanumeric = ('0'..'9') + ('a'..'z') + ('A'..'Z') + '_'

    /** The allowed digits for numbers. */
    private val digits = '0'..'9'

    override fun scan(source: String): Sequence<Token> = Tokenizer(source).tokenize()

    private inner class Tokenizer(private val source: String) {

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
                        if (match('/')) discardComment()
                        else yield(Token.Slash(startIdx))
                    }
                    '"' -> yieldAll(tokenizeString())
                    // must tokenize digits first, as identifiers cannot start with a digit
                    in digits -> yieldAll(tokenizeNumber())
                    in alphanumeric -> yieldAll(tokenizeReservedOrIdentifier())
                    ' ', '\t', '\r', '\n' -> {
                        // ignore whitespace
                    }
                    else -> onException(ScanException("Unexpected character: $ch", startIdx, 1))
                }
            }

            yield(Token.EOF(currentIdx))
        }

        private fun discardComment() {
            while (!isAtEnd() && peekOne() != '\n') advance()
        }

        private fun tokenizeString() = sequence<Token> {
            // consume all string characters
            while (!isAtEnd() && peekOne() != '"') advance()

            if (isAtEnd()) {
                onException(ScanException("Unterminated string", startIdx, currentIdx - startIdx))
            } else {
                // consume the closing '"'
                advance()
                yield(Token.String(startIdx, getLexeme().substring(1, currentIdx - startIdx - 1)))
            }
        }

        private fun tokenizeReservedOrIdentifier() = sequence<Token> {
            // consume reserved or identifier
            while (peekOne() in alphanumeric) advance()

            yield(getReservedWord(getLexeme()) ?: Token.Identifier(startIdx, getLexeme()))
        }

        private fun tokenizeNumber() = sequence<Token> {
            // consume whole part
            while (peekOne() in digits) advance()

            // look for fraction part
            if (peekOne() == '.' && peekTwo() in digits) {
                // consume '.'
                advance()
                // consume fraction part
                while (peekOne() in digits) advance()
            }

            yield(Token.Number(startIdx, getLexeme().toDouble()))
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
        private fun peekOne(): Char? = if (isAtEnd()) null else source[currentIdx]

        /**
         * Looks ahead two characters.
         *
         * Returns `null` when the next index from the current index is at the
         * end of the source text.
         */
        private fun peekTwo(): Char? = if (currentIdx + 1 >= source.length) null else source[currentIdx + 1]

        /**
         * Gets the [Token] associated with [lexeme] or `null` if [lexeme] is
         * not a reserved word.
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
