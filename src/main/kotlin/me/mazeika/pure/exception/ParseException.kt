package me.mazeika.pure.exception

import me.mazeika.pure.Token

class ParseException(message: String, token: Token) :
    PureException(message, token.offset, token.toString().length)
