package me.mazeika.pure.parse

import me.mazeika.pure.Token
import me.mazeika.pure.parse.Expression as EvalExpr

/** Represents a statement with side effects. */
sealed class Statement {

    data class Block(val stmts: List<Statement>) : Statement()

    data class Expression(val expr: EvalExpr) : Statement()

    data class Print(val expr: EvalExpr) : Statement()

    data class Variable(val name: Token.Identifier, val initializer: EvalExpr?) : Statement()
}




