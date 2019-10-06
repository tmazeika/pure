package me.mazeika.pure.exception

class ScanException(message: String, offset: Int, length: Int) :
    PureException(message, offset, length)
