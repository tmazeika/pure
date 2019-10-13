package me.mazeika.pure.parse

import me.mazeika.pure.Token
import me.mazeika.pure.parse.Expression as EvalExpr

/** Represents a statement with side effects. */
sealed class Statement {

    data class Block(val stmts: List<Statement>) : Statement()

    data class Expression(val expr: EvalExpr) : Statement()

    data class Function(val name: Token, val params: List<Token>, val body: Block) : Statement()

    data class If(val condition: EvalExpr, val thenBranch: Block, val elseBranch: Block?) : Statement()

    data class While(val condition: EvalExpr, val body: Block) : Statement()

    data class Print(val expr: EvalExpr) : Statement()

    data class Return(val keyword: Token, val value: EvalExpr?) : Statement()

    data class Variable(val name: Token.Identifier, val initializer: EvalExpr?) : Statement()
}




