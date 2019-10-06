package me.mazeika.pure.interpret

import me.mazeika.pure.Token
import me.mazeika.pure.exception.InterpretException
import me.mazeika.pure.exception.PureException
import me.mazeika.pure.parse.Expr

class PureInterpreter(private val out: Appendable) : Interpreter,
                                                     Expr.Visitor<Any?> {

    override fun interpret(expr: Expr, onException: (PureException) -> Unit) {
        try {
            val value = evaluate(expr)
            out.appendln(stringify(value))
        } catch (e: PureException) {
            onException(e)
        }
    }

    private fun stringify(value: Any?): String = if (value == null) "nil" else {
        val str = value.toString()

        when {
            value is Double && str.endsWith(".0") -> str.substring(
                0, str.length - 2
            )
            else -> str
        }
    }

    override fun visitBinary(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.op) {
            is Token.BangEqual -> !isEqual(left, right)
            is Token.EqualEqual -> isEqual(left, right)
            is Token.Greater -> checkNumberOperands(
                expr.op, left, right
            ).let { (left, right) -> left > right }
            is Token.GreaterEqual -> checkNumberOperands(
                expr.op, left, right
            ).let { (left, right) -> left >= right }
            is Token.Less -> checkNumberOperands(
                expr.op, left, right
            ).let { (left, right) -> left < right }
            is Token.LessEqual -> checkNumberOperands(
                expr.op, left, right
            ).let { (left, right) -> left <= right }
            is Token.Minus -> checkNumberOperands(
                expr.op, left, right
            ).let { (left, right) -> left - right }
            is Token.Plus -> when {
                left is Double && right is Double -> left + right
                left is String && right is String -> left + right
                left is Double && right is String || left is String && right is Double -> stringify(
                    left
                ) + stringify(right)
                else -> throw InterpretException(
                    "Operands must either be two numbers or two strings",
                    expr.op
                )
            }
            is Token.Slash -> checkNumberOperands(
                expr.op, left, right
            ).let { (left, right) ->
                if (right.compareTo(0) != 0) left / right
                else throw
                InterpretException("Denominator cannot be 0", expr.op)
            }
            is Token.Star -> checkNumberOperands(
                expr.op, left, right
            ).let { (left, right) -> left * right }
            else -> null
        }
    }

    override fun visitGrouping(expr: Expr.Grouping): Any? = evaluate(expr.expr)

    override fun visitLiteral(expr: Expr.Literal): Any? = expr.value

    override fun visitUnary(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)

        return when (expr.op) {
            is Token.Minus -> {
                -checkNumberOperand(expr.op, right)
            }
            is Token.Bang -> !isTruthy(right)
            else -> null
        }
    }

    private fun checkNumberOperand(op: Token, operand: Any?): Double =
        when (operand) {
            is Double -> operand
            else -> throw InterpretException("Operand must be a number", op)
        }

    private fun checkNumberOperands(
        op: Token, leftOperand: Any?, rightOperand: Any?
    ): Pair<Double, Double> = when {
        leftOperand is Double && rightOperand is Double -> Pair(
            leftOperand, rightOperand
        )
        else -> throw InterpretException("Operands must be numbers", op)
    }

    private fun isEqual(a: Any?, b: Any?): Boolean = when {
        a == null && b == null -> true
        a == null -> false
        else -> a == b
    }

    private fun isTruthy(value: Any?) =
        value != null && if (value is Boolean) value else true

    private fun evaluate(expr: Expr) = expr.accept(this)
}
