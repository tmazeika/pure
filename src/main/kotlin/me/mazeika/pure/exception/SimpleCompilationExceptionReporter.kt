package me.mazeika.pure.exception

/**
 * Represents a simple error reporter that says what went wrong and where at
 * compile time.
 */
class SimpleCompilationExceptionReporter(private val out: Appendable) :
    CompilationExceptionReporter {

    override fun report(sourceFile: String, exception: CompilationException) {
        val firstLineNum = exception.lines.first().first
        val lastLineNum = exception.lines.last().first

        out.appendln("Error: ${exception.message}")

        if (lastLineNum != firstLineNum) {
            out.appendln("Between lines $firstLineNum and $lastLineNum")
        } else {
            out.appendln("At line $firstLineNum")
        }
    }
}
