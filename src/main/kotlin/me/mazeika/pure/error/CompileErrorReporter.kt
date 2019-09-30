package me.mazeika.pure.error

/**
 * Represents a reporter of errors that occur at compile time.
 */
interface CompileErrorReporter {
    /**
     * Reports an error.
     *
     * @param message the informational message about what went wrong
     * @param sourceFile the name of the source file where the error occurred
     * @param sourceRow the row in the source file where the error occurred
     * @param sourceColumn the column in the source file where the error
     * occurred
     * @throws IllegalArgumentException when either [sourceRow] or
     * [sourceColumn] are not positive
     */
    fun report(
        message: String,
        sourceFile: String,
        sourceRow: Int,
        sourceColumn: Int
    )
}
