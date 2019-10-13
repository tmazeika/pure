package me.mazeika.pure.interpret

import me.mazeika.pure.Token
import me.mazeika.pure.exception.InterpretException

sealed class Environment {

    private val map: MutableMap<Token, Any?> = HashMap()

    fun define(ident: Token, value: Any?) {
        this.map[ident] = value
    }

    abstract fun redefine(ident: Token, value: Any?)

    abstract fun lookUp(ident: Token): Any?

    abstract fun getGlobal(): Global

    class Global : Environment() {

        override fun redefine(ident: Token, value: Any?) {
            if (super.map.contains(ident)) super.map[ident] = value
            else throw InterpretException("Undefined identifier '$ident'", ident)
        }

        override fun lookUp(ident: Token): Any? {
            if (super.map.contains(ident)) return super.map[ident]
            else throw InterpretException("Undefined identifier '$ident'", ident)
        }

        override fun getGlobal(): Global = this
    }

    class Local(private val parent: Environment) : Environment() {

        override fun redefine(ident: Token, value: Any?) {
            if (super.map.contains(ident)) super.map[ident] = value
            else this.parent.redefine(ident, value)
        }

        override fun lookUp(ident: Token): Any? =
            if (super.map.contains(ident)) super.map[ident]
            else this.parent.lookUp(ident)

        override fun getGlobal(): Global = parent.getGlobal()
    }
}
