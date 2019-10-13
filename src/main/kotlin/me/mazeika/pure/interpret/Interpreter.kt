package me.mazeika.pure.interpret

import me.mazeika.pure.parse.Statement

interface Interpreter {

    fun interpret(stmts: Sequence<Statement>)

    companion object {
        fun createDefault(out: Appendable): Interpreter = DefaultInterpreter(Environment.Global(), out)
    }
}
