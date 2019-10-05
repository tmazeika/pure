package me.mazeika.pure.parse

/** Prints abstract syntax trees (AST). */
object AstPrinter : Expr.Visitor<String> {

    /** Prints the given top-level [expr] recursively. **/
    fun print(expr: Expr) = expr.accept(this)

    override fun visitBinary(expr: Expr.Binary): String =
        parenthesize(expr.op.lexeme, expr.left, expr.right)

    override fun visitGrouping(expr: Expr.Grouping): String =
        parenthesize("group", expr.expr)

    override fun visitLiteral(expr: Expr.Literal): String =
        expr.token.value.toString()

    override fun visitUnary(expr: Expr.Unary): String =
        parenthesize(expr.op.lexeme, expr.right)

    /**
     * Puts parenthesis around the given [exprs] applied to the function named
     * [name], recursively evaluating all [exprs].
     */
    private fun parenthesize(name: String, vararg exprs: Expr): String {
        val sb = StringBuilder("(").append(name)
        for (expr in exprs) sb.append(' ').append(expr.accept(this))
        return sb.append(')').toString()
    }
}
