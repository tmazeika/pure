package me.mazeika.pure.scan

import io.kotlintest.fail
import io.kotlintest.specs.StringSpec

class PureScannerTest : StringSpec({

    "scanTokens should handle basic operators and comments" {
        val scanner: Scanner = PureScanner
        val tokens = scanner.scanTokens(
            "// this is a comment\n(( )){} // grouping stuff\n" +
                    "!*+-/=<> <= == // operators"
        ) { e: Exception ->
            fail("Received exception during scan: $e")
        }

        tokens.first()

        // tokens.shouldContainAll(
        //     LeftParenToken("(", 21),
        //     LeftParenToken("(", 22),
        //     RightParenToken(")", 24),
        //     RightParenToken(")", 25),
        //     LeftBraceToken("{", 26),
        //     RightBraceToken("}", 27)
        // )
    }
})
