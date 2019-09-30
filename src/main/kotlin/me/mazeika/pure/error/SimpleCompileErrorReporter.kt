package me.mazeika.pure.error

/**
 * Represents a simple error reporter that says what went wrong and where at
 * compile time.
 */
class SimpleCompileErrorReporter(private val out: Appendable) :
    CompileErrorReporter {

    override fun report(
        message: String,
        sourceFile: String,
        sourceRow: Int,
        sourceColumn: Int
    ) {
        require(sourceRow > 0)
        require(sourceColumn > 0)

        out.appendln("Error at $sourceFile:$sourceRow:$sourceColumn")
        out.appendln(message)
    }
}
