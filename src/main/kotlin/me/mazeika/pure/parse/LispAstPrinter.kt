package me.mazeika.pure.parse

internal class LispAstPrinter : AstPrinter {

    /** Converts an [expr] to a String recursively. **/
    override fun print(expr: Expression?): String = when (expr) {
        is Expression.Binary -> parenthesize(expr.op.toString(), expr.left, expr.right)
        is Expression.Grouping -> parenthesize("group", expr.expr)
        is Expression.Literal -> expr.toString()
        is Expression.Unary -> parenthesize(expr.op.toString(), expr.right)
        is Expression.Variable -> expr.toString()
        null -> "nil"
    }

    private fun parenthesize(name: String, vararg exprs: Expression): String {
        val sb = StringBuilder("(").append(name)
        for (expr in exprs) sb.append(' ').append(this.print(expr))
        return sb.append(')').toString()
    }
}
