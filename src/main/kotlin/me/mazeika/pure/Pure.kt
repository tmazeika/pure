package me.mazeika.pure

import me.mazeika.pure.exception.ExceptionReporter
import me.mazeika.pure.exception.PureException
import me.mazeika.pure.exception.SimpleExceptionReporter
import me.mazeika.pure.interpret.Interpreter
import me.mazeika.pure.interpret.PureInterpreter
import me.mazeika.pure.parse.PureParser
import me.mazeika.pure.scan.PureScanner
import me.mazeika.pure.scan.Scanner

fun main() {
    while (true) {
        val line = readLine()

        if (line == null) {
            println("Closing...")
            break
        } else {
            var error = false

            val scanner: Scanner = PureScanner(line)
            val reporter: ExceptionReporter = SimpleExceptionReporter(
                System.out, "stdin", line, 15
            )
            fun onException(e: PureException) {
                error = true
                reporter.report(e)
            }

            val expr = scanner.tokenize(::onException).toList().let {
                PureParser(it).parse(::onException)
            }

            if (error || expr == null) {
                continue
            }

            // println(AstPrinter.print(expr))

            val interpreter: Interpreter = PureInterpreter(System.out)

            interpreter.interpret(expr, ::onException)
        }
    }
}
