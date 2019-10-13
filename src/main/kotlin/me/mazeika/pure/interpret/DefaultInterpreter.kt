package me.mazeika.pure.interpret

import me.mazeika.pure.Token
import me.mazeika.pure.exception.InterpretException
import me.mazeika.pure.parse.Expression
import me.mazeika.pure.parse.Statement

internal class DefaultInterpreter(private val env: Environment.Global, private val out: Appendable) : Interpreter {

    init {
        this.env.define(Token.Identifier(0, "clock"), object : PureCallable {
            override val arity: Int = 0

            override fun call(interpreter: Interpreter, env: Environment.Global, args: List<Any?>): Any? {
                return System.currentTimeMillis().toDouble() / 1000.0
            }

            override fun toString(): String = "<native fn>"
        })
    }

    fun Any?.discard() = Unit

    override fun interpret(env: Environment, stmts: Sequence<Statement>) {
        stmts.forEach { this.execute(env, it) }
    }

    override fun interpret(stmts: Sequence<Statement>) = this.interpret(env, stmts)

    private fun execute(env: Environment, stmt: Statement): Unit = when (stmt) {
        is Statement.Block -> {
            val newEnv: Environment = Environment.Local(env)
            stmt.stmts.forEach { this.execute(newEnv, it) }
        }
        is Statement.Expression -> {
            this.evaluate(env, stmt.expr)
        }
        is Statement.Function -> {
            val function = PureFunction(stmt)
            env.define(stmt.name, function)
        }
        is Statement.If -> when {
            this.isTruthy(this.evaluate(env, stmt.condition)) -> this.execute(env, stmt.thenBranch)
            stmt.elseBranch != null -> this.execute(env, stmt.elseBranch)
            else -> {
            }
        }
        is Statement.While -> {
            while (this.isTruthy(this.evaluate(env, stmt.condition))) {
                this.execute(env, stmt.body)
            }
        }
        is Statement.Print -> {
            this.out.appendln(this.stringify(this.evaluate(env, stmt.expr)))
        }
        is Statement.Variable -> {
            val value = when {
                stmt.initializer != null -> this.evaluate(env, stmt.initializer)
                else -> null
            }
            env.define(stmt.name, value)
        }
    }.discard()

    private fun evaluate(env: Environment, expr: Expression): Any? = when (expr) {
        is Expression.Assign -> {
            val right = this.evaluate(env, expr.value)
            env.redefine(expr.name, right)
            right
        }
        is Expression.Binary -> this.evaluateBinary(env, expr)
        is Expression.Call -> {
            val callee = this.evaluate(env, expr.callee)
            val args = mutableListOf<Any?>()
            expr.args.forEach { args.add(this.evaluate(env, it)) }

            if (callee !is PureCallable) {
                throw InterpretException("Can only call functions and classes", expr.paren)
            }

            if (args.size != callee.arity) {
                throw InterpretException("Expected ${callee.arity} function arguments but got ${args.size}", expr.paren)
            }
            callee.call(this, env.getGlobal(), args)
        }
        is Expression.Grouping -> this.evaluate(env, expr.expr)
        is Expression.Literal -> expr.value
        is Expression.Logical -> {
            val left = this.evaluate(env, expr.left)

            if (expr.op is Token.Or) {
                if (isTruthy(left)) left else this.evaluate(env, expr.right)
            } else if (!isTruthy(left)) left else this.evaluate(env, expr.right)
        }
        is Expression.Unary -> {
            val right = this.evaluate(env, expr.right)

            when (expr.op) {
                is Token.Minus -> -this.checkNumberOperand(expr.op, right)
                is Token.Bang -> !this.isTruthy(right)
                else -> null
            }
        }
        is Expression.Variable -> env.lookUp(expr.name)
    }

    private fun stringify(value: Any?): String = if (value == null) "nil" else {
        val str = value.toString()

        when {
            value is Double && str.endsWith(".0") -> str.substring(0, str.length - 2)
            else -> str
        }
    }

    private fun evaluateBinary(env: Environment, expr: Expression.Binary): Any? {
        val left = this.evaluate(env, expr.left)
        val right = this.evaluate(env, expr.right)

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
