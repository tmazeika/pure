package me.mazeika.pure.exception

import me.mazeika.pure.Token

class InterpretException(message: String, token: Token) :
    PureException(message, token.offset, token.toString().length)
