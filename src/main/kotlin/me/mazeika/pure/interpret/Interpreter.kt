package me.mazeika.pure.interpret

import me.mazeika.pure.exception.PureException
import me.mazeika.pure.parse.Stmt

interface Interpreter {

    fun interpret(stmts: Sequence<Stmt>, onException: (PureException) -> Unit)
}
