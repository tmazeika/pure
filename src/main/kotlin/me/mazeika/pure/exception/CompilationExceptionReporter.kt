package me.mazeika.pure.exception

/**
 * Represents a reporter of compile time exceptions.
 */
interface CompilationExceptionReporter {
    /**
     * Reports a compilation exception in the given source file.
     *
     * @param sourceFile the source file where the exception occurred
     * @param exception the exception
     */
    fun report(sourceFile: String, exception: CompilationException)
}
