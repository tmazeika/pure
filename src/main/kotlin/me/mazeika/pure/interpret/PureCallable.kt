package me.mazeika.pure.interpret

internal interface PureCallable {
    val arity: Int

    fun call(interpreter: Interpreter, env: Environment.Global, args: List<Any?>): Any?
}
