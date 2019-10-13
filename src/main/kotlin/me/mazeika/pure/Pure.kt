package me.mazeika.pure

import me.mazeika.pure.exception.ExceptionReporter
import me.mazeika.pure.exception.GraphicalExceptionReporter
import me.mazeika.pure.exception.PureException
import me.mazeika.pure.interpret.Interpreter
import me.mazeika.pure.parse.Parser
import me.mazeika.pure.scan.Scanner

fun main() = repl()

fun repl() = repl(Interpreter.createDefault(System.out))

tailrec fun repl(interpreter: Interpreter) {
    val line: String = readLine() ?: return

    execute(interpreter, line, GraphicalExceptionReporter(System.out, "stdin", line, 30))
    repl(interpreter)
}

fun execute(interpreter: Interpreter, source: String, exceptionReporter: ExceptionReporter) {
    var error = false
    fun exceptionTracker(e: PureException) {
        error = true
        exceptionReporter.report(e)
    }

    val tokens = Scanner.createDefault(::exceptionTracker).scan(source).toList()
    val stmts = Parser.createDefault(::exceptionTracker).parse(tokens)

    if (!error) try {
        interpreter.interpret(stmts)
    } catch (e: PureException) {
        exceptionReporter.report(e)
    }
}
