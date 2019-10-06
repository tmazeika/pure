package me.mazeika.pure.interpret

import me.mazeika.pure.exception.PureException
import me.mazeika.pure.parse.Expr

interface Interpreter {

    fun interpret(expr: Expr, onException: (PureException) -> Unit)
}
