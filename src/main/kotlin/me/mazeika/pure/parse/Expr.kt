package me.mazeika.pure.parse

import me.mazeika.pure.Token

/** Represents an expression that can be evaluated. */
sealed class Expr {

    /** Accepts a visitor. */
    abstract fun <R> accept(visitor: Visitor<R>): R

    interface Visitor<out R> {
        fun visitBinary(expr: Binary): R
        fun visitGrouping(expr: Grouping): R
        fun <T> visitLiteral(expr: Literal<T>): R
        fun visitUnary(expr: Unary): R
    }

    class Binary(val left: Expr, val op: Token, val right: Expr) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R =
            visitor.visitBinary(this)
    }

    class Grouping(val expr: Expr) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R =
            visitor.visitGrouping(this)
    }

    class Literal<T>(val value: T) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R =
            visitor.visitLiteral(this)
    }

    class Unary(val op: Token, val right: Expr) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R =
            visitor.visitUnary(this)
    }
}




