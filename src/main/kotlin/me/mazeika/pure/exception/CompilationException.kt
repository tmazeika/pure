package me.mazeika.pure.exception

/**
 * Represents a compile time exception.
 *
 * @param message the informational message about what went wrong
 * @param source the entire source text
 * @param offset the start offset of the offending character(s) in [source]
 * @param length the number of offending characters
 * @throws IllegalArgumentException when [offset] is negative, [length] is less
 * than 1, or the sum of [offset] and [length] is greater than the length of
 * [source]
 */
class CompilationException(
    message: String,
    private val source: String,
    private val offset: Int,
    private val length: Int
) : Exception(message) {

    init {
        require(offset >= 0)
        require(length > 0)
        require(offset + length <= source.length)
    }

    /**
     * Gets a list of pairs of line numbers and the source text of that line.
     */
    val lines: List<Pair<Int, String>> by lazy {
        val firstLineNum: Int =
            source.substring(0, offset).count { it == '\n' } + 1
        val lastLineNum: Int = firstLineNum +
                source.substring(offset, offset + length).count { it == '\n' }
        val firstLineIdx: Int =
            if (offset == 0)
                0
            else
                source.lastIndexOf('\n', offset - 1)
        val lastLineIdx: Int =
            if (offset + length == source.length)
                offset + length
            else
                source.indexOf('\n', offset + length + 1)
        val lineStrs: List<String> = source.substring(
            if (firstLineIdx == -1) 0 else firstLineIdx,
            if (lastLineIdx == -1) source.length else lastLineIdx
        ).split('\n')

        assert(lineStrs.size == lastLineNum - firstLineNum + 1)

        IntRange(firstLineNum, lastLineNum).zip(lineStrs)
    }
}
