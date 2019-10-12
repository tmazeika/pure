package me.mazeika.pure.exception

import kotlin.math.max
import kotlin.math.min

/**
 * Represents a graphical exception reporter that reports what went wrong while
 * compiling the source file named [sourceName] with text [sourceText].
 *
 * It is the caller's responsibility to make sure that the errors reported
 * through [report] belong to the source file. When the lines are printed,
 * [padding] specifies the number of extra characters to print on each side
 * surrounding the lexeme(s) of interest. E.g. `padding=3` prints the three
 * characters to the left of the lexeme and the three characters to the right.
 */
class GraphicalExceptionReporter(
    private val out: Appendable,
    private val sourceName: String,
    private val sourceText: String,
    private val padding: Int
) : ExceptionReporter {

    override fun report(e: PureException) {
        require(e.offset + e.length <= sourceText.length)

        val firstLineIdx =
            sourceText.substring(0, e.offset).count { it == '\n' }
        val lastLineIdx = firstLineIdx + sourceText.substring(
            e.offset, e.offset + e.length
        ).count { it == '\n' }

        val lineNumLen = (lastLineIdx + 1).toString().length

        val firstNL = sourceText.lastIndexOf('\n', e.offset - 1)
        val lastNL = sourceText.indexOf('\n', e.offset + e.length)

        var leftIdx = max(0, e.offset - padding)
        if (firstNL > leftIdx) leftIdx = firstNL

        var rightIdx = min(sourceText.length, e.offset + e.length + padding)
        if (lastNL != -1 && lastNL < rightIdx) rightIdx = lastNL

        val leftPrefix = if (leftIdx > 0 && sourceText[leftIdx - 1] != '\n') {
            "..."
        } else {
            ""
        }
        val rightPrefix =
            if (rightIdx < sourceText.length && sourceText[rightIdx] != '\n') {
                "..."
            } else {
                ""
            }

        out.appendln("Error in <$sourceName>: ${e.message}").appendln()
            .append("\t").append(
                " ".repeat(
                    lineNumLen + 3 + e.offset - leftIdx + leftPrefix.length
                )
            ).append(
                "⤹ here", if (e.length > 1) " (${e.length} characters)" else ""
            ).appendln()

        sourceText.substring(leftIdx, rightIdx).lines()
            .forEachIndexed { lineIdx, line ->
                val lineNum = firstLineIdx + lineIdx + 1
                out.appendln(
                    "\t%${lineNumLen}d | $leftPrefix$line$rightPrefix".format(
                        lineNum
                    )
                )
            }
        out.appendln()
    }
}
