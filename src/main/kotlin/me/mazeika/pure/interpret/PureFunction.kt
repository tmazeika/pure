package me.mazeika.pure.interpret

import me.mazeika.pure.parse.Statement

internal class PureFunction(private val declaration: Statement.Function, private val closure: Environment) : PureCallable {
    override val arity: Int = declaration.params.size

    override fun call(interpreter: Interpreter, args: List<Any?>): Any? {
        val newEnv = Environment.Local(closure)
        declaration.params.forEachIndexed { idx, param ->
            newEnv.define(param, args[idx])
        }
        try {
            interpreter.interpret(newEnv, declaration.body.stmts.asSequence())
        } catch (r: Return) {
            return r.value
        }
        return null
    }

    override fun toString(): String = "<fn ${declaration.name.lexeme}>"
}
