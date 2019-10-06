package me.mazeika.pure.parse

import me.mazeika.pure.Token
import me.mazeika.pure.exception.ParseException
import me.mazeika.pure.exception.PureException

class PureParser(private val tokens: List<Token>) : Parser {

    override fun parse(onException: (e: PureException) -> Unit): Expr? = try {
        TokensToExpr().expr()
    } catch (e: PureException) {
        onException(e)
        null
    }

    private inner class TokensToExpr {
        private var currentIdx = 0

        fun expr(): Expr = equality()

        private fun equality(): Expr {
            var expr: Expr = comparison()

            while (match<Token.BangEqual>() || match<Token.EqualEqual>()) {
                expr = Expr.Binary(expr, previous(), comparison())
            }

            return expr
        }

        private fun comparison(): Expr {
            var expr: Expr = addition()

            while (match<Token.Greater>() || match<Token.GreaterEqual>() || match<Token.Less>() || match<Token.LessEqual>()) {
                expr = Expr.Binary(expr, previous(), addition())
            }

            return expr
        }

        private fun addition(): Expr {
            var expr: Expr = multiplication()

            while (match<Token.Minus>() || match<Token.Plus>()) {
                expr = Expr.Binary(expr, previous(), multiplication())
            }

            return expr;
        }

        private fun multiplication(): Expr {
            var expr: Expr = unary()

            while (match<Token.Slash>() || match<Token.Star>()) {
                expr = Expr.Binary(expr, previous(), unary())
            }

            return expr
        }

        private fun unary(): Expr = when {
            match<Token.Bang>() || match<Token.Minus>() -> Expr.Unary(
                previous(), unary()
            )
            else -> atom()
        }

        private fun atom(): Expr = when {
            match<Token.False>() -> Expr.Literal(false)
            match<Token.True>() -> Expr.Literal(true)
            match<Token.Nil>() -> Expr.Literal(null)
            match<Token.Number>() || match<Token.String>() -> Expr.Literal(
                (previous() as Token.Literal<*>).value // TODO: I don't like this
            )
            match<Token.LeftParen>() -> {
                val expr: Expr = expr()

                consume<Token.RightParen>(
                    "Expected closing ')' after expression"
                )
                Expr.Grouping(expr)
            }
            else -> throw ParseException("Expected expression", peek())
        }

        private inline fun <reified T : Token> check(): Boolean =
            !isAtEnd() && peek() is T

        private inline fun <reified T : Token> match(): Boolean = when {
            check<T>() -> {
                advance()
                true
            }
            else -> false
        }

        private inline fun <reified T : Token> consume(message: String): Token =
            when {
                check<T>() -> advance()
                else -> throw ParseException(message, peek())
            }

        private fun advance(): Token {
            if (!isAtEnd()) currentIdx++
            return previous()
        }

        private fun peek(): Token = tokens[currentIdx]

        private fun previous(): Token = tokens[currentIdx - 1]

        private fun isAtEnd(): Boolean = peek() is Token.EOF

        private fun synchronize() {
            advance()

            while (!isAtEnd()) {
                if (previous() is Token.Semicolon) return

                // @formatter:off
                when (peek()) {
                    is Token.Class,
                    is Token.Fun,
                    is Token.Var,
                    is Token.For,
                    is Token.If,
                    is Token.While,
                    is Token.Print,
                    is Token.Return ->
                        return
                    else ->
                        advance()
                }
                // @formatter:on
            }
        }
    }
}
