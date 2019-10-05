package me.mazeika.pure.scan

import me.mazeika.pure.Token
import me.mazeika.pure.exception.CompilationException

interface Scanner {
    /**
     * Scans all tokens from the given source text.
     *
     * @param source the full source text
     * @param onException the callback function on compilation exceptions; may
     * be called multiple times; tokens will still continue to be scanned as
     * normal even when this function is called
     */
    fun scanTokens(
        source: String,
        onException: (e: CompilationException) -> Unit
    ): Sequence<Token>
}
