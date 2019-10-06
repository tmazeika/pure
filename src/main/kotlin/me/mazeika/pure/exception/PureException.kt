package me.mazeika.pure.exception

abstract class PureException(
    message: String,
    val offset: Int,
    val length: Int
) : Exception(message) {

    init {
        require(offset >= 0)
        require(length >= 0)
    }
}
