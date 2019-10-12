package me.mazeika.pure.interpret

import me.mazeika.pure.Token
import me.mazeika.pure.exception.InterpretException

internal class Environment {

    private val map: MutableMap<Token, Any?> = HashMap()

    fun define(ident: Token, value: Any?) {
        map[ident] = value
    }

    fun redefine(ident: Token, value: Any?) {
        if (map.contains(ident)) map[ident] = value
        else throw InterpretException("Undefined identifier '$ident'", ident)
    }

    fun lookUp(ident: Token): Any? =
        if (map.contains(ident)) map[ident]
        else throw InterpretException("Undefined identifier '$ident'", ident)
}
