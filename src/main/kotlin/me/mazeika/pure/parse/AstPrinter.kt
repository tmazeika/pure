package me.mazeika.pure.parse

/** Represents a printer of abstract syntax trees (AST). */
interface AstPrinter {

    /** Recursively prints an [expr] as a String. */
    fun print(expr: Expression?): String

    companion object {
        fun createLisp(): AstPrinter = LispAstPrinter()
    }
}
