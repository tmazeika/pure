package me.mazeika.pure.interpret

import me.mazeika.pure.parse.Statement

interface Interpreter {

    fun interpret(stmts: Sequence<Statement>)

    companion object {
        fun createDefaultInterpreter(out: Appendable): Interpreter = DefaultInterpreter(out)
    }
}
