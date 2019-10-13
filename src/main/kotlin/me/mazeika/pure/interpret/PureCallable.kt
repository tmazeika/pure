package me.mazeika.pure.interpret

internal interface PureCallable {
    val arity: Int

    fun call(interpreter: Interpreter, args: List<Any?>): Any?
}
