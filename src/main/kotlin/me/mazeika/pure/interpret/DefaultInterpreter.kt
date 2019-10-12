package me.mazeika.pure.interpret

import me.mazeika.pure.Token
import me.mazeika.pure.exception.InterpretException
import me.mazeika.pure.parse.AstPrinter
import me.mazeika.pure.parse.Expression
import me.mazeika.pure.parse.Statement

internal class DefaultInterpreter(private val out: Appendable) : Interpreter {

    private val env = Environment()

    @Suppress("unused")
    fun Any?.discard() = Unit

    override fun interpret(stmts: Sequence<Statement>) = stmts.forEach(::execute)

    private fun execute(stmt: Statement) = when (stmt) {
        is Statement.Expression -> {
            out.appendln("--- Evaluating: ${AstPrinter.createLisp().print(stmt.expr)}")
            this.evaluate(stmt.expr)
        }
        is Statement.Print -> {
            out.appendln("--- Evaluating: ${AstPrinter.createLisp().print(stmt.expr)}")
            this.out.appendln(this.stringify(this.evaluate(stmt.expr)))
        }
        is Statement.Variable -> {
            val value = when {
                stmt.initializer != null -> this.evaluate(stmt.initializer)
                else -> null
            }
            out.appendln("--- Defining '${stmt.name}' with: ${AstPrinter.createLisp().print(stmt.initializer)}")
            this.env.define(stmt.name, value)
        }
    }.discard()

    private fun evaluate(expr: Expression): Any? = when (expr) {
        is Expression.Binary -> this.evaluateBinary(expr)
        is Expression.Grouping -> this.evaluate(expr.expr)
        is Expression.Literal -> expr.value
        is Expression.Unary -> {
            val right = evaluate(expr.right)

            when (expr.op) {
                is Token.Minus -> -this.checkNumberOperand(expr.op, right)
                is Token.Bang -> !this.isTruthy(right)
                else -> null
            }
        }
        is Expression.Variable -> this.env.lookUp(expr.name)
    }

    private fun stringify(value: Any?): String = if (value == null) "nil" else {
        val str = value.toString()

        when {
            value is Double && str.endsWith(".0") -> str.substring(0, str.length - 2)
            else -> str
        }
    }

    private fun evaluateBinary(expr: Expression.Binary): Any? {
        val left = this.evaluate(expr.left)
        val right = this.evaluate(expr.right)

        return when (expr.op) {
            is Token.BangEqual -> !this.isEqual(left, right)
            is Token.EqualEqual -> this.isEqual(left, right)
            is Token.Greater -> {
                val (l, r) = this.checkNumberOperands(expr.op, left, right)
                l > r
            }
            is Token.GreaterEqual -> {
                val (l, r) = this.checkNumberOperands(expr.op, left, right)
                l >= r
            }
            is Token.Less -> {
                val (l, r) = this.checkNumberOperands(expr.op, left, right)
                l < r
            }
            is Token.LessEqual -> {
                val (l, r) = this.checkNumberOperands(expr.op, left, right)
                l <= r
            }
            is Token.Minus -> {
                val (l, r) = this.checkNumberOperands(expr.op, left, right)
                l - r
            }
            is Token.Plus -> when {
                left is Double && right is Double -> left + right
                left is String && right is String -> left + right
                else -> throw InterpretException("Operands must either be two numbers or two strings", expr.op)
            }
            is Token.Slash -> {
                val (l, r) = this.checkNumberOperands(expr.op, left, right)
                if (r.compareTo(0) != 0) l / r else throw InterpretException("Cannot divide by 0", expr.op)
            }
            is Token.Star -> {
                val (l, r) = this.checkNumberOperands(expr.op, left, right)
                l * r
            }
            else -> null
        }
    }

    private fun checkNumberOperand(op: Token, operand: Any?): Double = when (operand) {
        is Double -> operand
        else -> throw InterpretException("Operand must be a number", op)
    }

    private fun checkNumberOperands(op: Token, left: Any?, right: Any?): Pair<Double, Double> = when {
        left is Double && right is Double -> Pair(left, right)
        else -> throw InterpretException("Operands must be numbers", op)
    }

    private fun isEqual(a: Any?, b: Any?): Boolean = when {
        a == null && b == null -> true
        a == null -> false
        else -> a == b
    }

    private fun isTruthy(value: Any?) = value != null && if (value is Boolean) value else true
}
