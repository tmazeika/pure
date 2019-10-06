package me.mazeika.pure.parse

import me.mazeika.pure.parse.Expr as EvalExpr

/** Represents a statement with side effects. */
sealed class Stmt {

    /** Accepts a visitor. */
    abstract fun <R> accept(visitor: Visitor<R>): R

    interface Visitor<out R> {
        fun visitExpr(stmt: Expr): R
        fun visitPrint(stmt: Print): R
    }

    class Expr(val expr: EvalExpr) : Stmt() {

        override fun <R> accept(visitor: Visitor<R>): R =
            visitor.visitExpr(this)
    }

    class Print(val expr: EvalExpr) : Stmt() {

        override fun <R> accept(visitor: Visitor<R>): R =
            visitor.visitPrint(this)
    }
}




