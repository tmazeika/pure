package me.mazeika.pure.parse

import me.mazeika.pure.Token
import me.mazeika.pure.exception.ParseException
import me.mazeika.pure.exception.PureException

class DefaultParser(private val onException: (PureException) -> Unit) : Parser {

    override fun parse(tokens: List<Token>): Sequence<Statement> = StatementBuilder(tokens).buildAll()

    private inner class StatementBuilder(private val tokens: List<Token>) {

        private var currentIdx = 0

        fun buildAll(): Sequence<Statement> = sequence {
            while (!isAtEnd()) {
                try {
                    yield(declaration())
                } catch (e: PureException) {
                    synchronize()
                    onException(e)
                }
            }
        }

        private fun declaration(): Statement = if (match<Token.Var>()) varDeclaration() else stmt()

        private fun varDeclaration(): Statement {
            val name = consume<Token.Identifier>("Expected identifier")

            var initializer: Expression? = null
            if (match<Token.Equal>()) {
                initializer = expr()
            }

            consume<Token.Semicolon>("Expected ';' after variable declaration")
            return Statement.Variable(name, initializer)
        }

        private fun stmt(): Statement = when {
            match<Token.If>() -> ifStmt()
            match<Token.While>() -> whileStmt()
            match<Token.Print>() -> printStmt()
            match<Token.LeftBrace>() -> Statement.Block(block().toList())
            else -> exprStmt()
        }

        private fun ifStmt(): Statement {
            val condition: Expression = expr()
            consume<Token.LeftBrace>("Expected '{' after if condition")

            val thenBranch: Statement.Block = Statement.Block(block().toList())
            var elseBranch: Statement.Block? = null
            if (match<Token.Else>()) {
                consume<Token.LeftBrace>("Expected '{' after 'else'")
                elseBranch = Statement.Block(block().toList())
            }
            return Statement.If(condition, thenBranch, elseBranch)
        }

        private fun whileStmt(): Statement {
            val condition: Expression = expr()
            consume<Token.LeftBrace>("Expected '{' after while condition")

            val body: Statement.Block = Statement.Block(block().toList())
            return Statement.While(condition, body)
        }

        private fun printStmt(): Statement {
            val expr: Expression = expr()
            consume<Token.Semicolon>("Expected ';' after statement")
            return Statement.Print(expr)
        }

        private fun block(): Sequence<Statement> = sequence {
            while (!check<Token.RightBrace>() && !isAtEnd()) {
                yield(declaration())
            }
            consume<Token.RightBrace>("Expected '}' after block")
        }

        private fun exprStmt(): Statement {
            val expr = expr()
            consume<Token.Semicolon>("Expected ';' after expression statement")
            return Statement.Expression(expr)
        }

        private fun expr(): Expression = assignment()

        private fun assignment(): Expression {
            val expr = or()

            if (match<Token.Equal>()) {
                val equals = previous()
                val value = assignment()

                if (expr is Expression.Variable) {
                    return Expression.Assign(expr.name, value)
                }

                onException(ParseException("Invalid assignment target", equals))
            }

            return expr
        }

        private fun or(): Expression {
            var expr: Expression = and()
            while (match<Token.Or>()) {
                val op: Token = previous()
                val right: Expression = and()
                expr = Expression.Logical(expr, op, right)
            }
            return expr
        }

        private fun and(): Expression {
            var expr: Expression = equality()
            while (match<Token.And>()) {
                val op: Token = previous()
                val right: Expression = equality()
                expr = Expression.Logical(expr, op, right)
            }
            return expr
        }

        private fun equality(): Expression {
            var expr: Expression = comparison()
            while (match<Token.BangEqual>() || match<Token.EqualEqual>()) {
                expr = Expression.Binary(expr, previous(), comparison())
            }
            return expr
        }

        private fun comparison(): Expression {
            var expr: Expression = addition()
            // @formatter:off
            while (match<Token.Greater>()
                    || match<Token.GreaterEqual>()
                    || match<Token.Less>()
                    || match<Token.LessEqual>()) {
                expr = Expression.Binary(expr, previous(), addition())
            }
            // @formatter:on
            return expr
        }

        private fun addition(): Expression {
            var expr: Expression = multiplication()
            while (match<Token.Minus>() || match<Token.Plus>()) {
                expr = Expression.Binary(expr, previous(), multiplication())
            }
            return expr
        }

        private fun multiplication(): Expression {
            var expr: Expression = unary()
            while (match<Token.Slash>() || match<Token.Star>()) {
                expr = Expression.Binary(expr, previous(), unary())
            }
            return expr
        }

        private fun unary(): Expression = when {
            match<Token.Bang>() || match<Token.Minus>() -> Expression.Unary(previous(), unary())
            else -> atom()
        }

        private fun atom(): Expression = when {
            match<Token.False>() -> Expression.Literal(false)
            match<Token.True>() -> Expression.Literal(true)
            match<Token.Nil>() -> Expression.Literal(null)
            match<Token.Number>() || match<Token.String>() -> Expression.Literal((previous() as Token.Literal<*>).value)
            match<Token.LeftParen>() -> {
                val expr: Expression = expr()
                consume<Token.RightParen>("Expected closing ')' after expression")
                Expression.Grouping(expr)
            }
            match<Token.Identifier>() -> Expression.Variable(previous())
            else -> throw ParseException("Expected expression", peek())
        }

        private inline fun <reified T : Token> check(): Boolean = !isAtEnd() && peek() is T

        private inline fun <reified T : Token> match(): Boolean = when {
            check<T>() -> {
                advance()
                true
            }
            else -> false
        }

        private inline fun <reified T : Token> consume(message: String): T = when {
            check<T>() -> advance() as T
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
                    is Token.Return -> return
                    else -> advance()
                }
                // @formatter:on
            }
        }
    }
}
