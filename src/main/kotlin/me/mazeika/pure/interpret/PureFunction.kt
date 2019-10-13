package me.mazeika.pure.interpret

import me.mazeika.pure.parse.Statement

internal class PureFunction(private val declaration: Statement.Function) : PureCallable {
    override val arity: Int = declaration.params.size

    override fun call(interpreter: Interpreter, env: Environment.Global, args: List<Any?>): Any? {
        val newEnv = Environment.Local(env)
        declaration.params.forEachIndexed { idx, param ->
            newEnv.define(param, args[idx])
        }
        interpreter.interpret(newEnv, declaration.body.stmts.asSequence())
        return null
    }

    override fun toString(): String = "<fn ${declaration.name.lexeme}>"
}
