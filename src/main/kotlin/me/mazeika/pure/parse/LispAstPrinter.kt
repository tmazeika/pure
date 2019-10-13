package me.mazeika.pure.parse

internal class LispAstPrinter : AstPrinter {

    /** Converts an [expr] to a String recursively. **/
    override fun print(expr: Expression?): String = when (expr) {
        is Expression.Assign -> this.print(expr.value)
        is Expression.Binary -> parenthesize(expr.op.lexeme, expr.left, expr.right)
        is Expression.Grouping -> parenthesize("group", expr.expr)
        is Expression.Literal -> expr.toString()
        is Expression.Logical -> parenthesize(expr.op.lexeme, expr.left, expr.right)
        is Expression.Unary -> parenthesize(expr.op.lexeme, expr.right)
        is Expression.Variable -> expr.toString()
        null -> "nil"
    }

    private fun parenthesize(name: String, vararg exprs: Expression): String {
        val sb = StringBuilder("(").append(name)
        for (expr in exprs) sb.append(' ').append(this.print(expr))
        return sb.append(')').toString()
    }
}
