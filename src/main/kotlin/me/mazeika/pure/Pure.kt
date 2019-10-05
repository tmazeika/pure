package me.mazeika.pure

import me.mazeika.pure.parse.AstPrinter
import me.mazeika.pure.parse.Expr
import me.mazeika.pure.scan.PureScanner

fun main() {
    while (true) {
        val line = readLine()

        if (line == null) {
            println("Closing...")
            break
        } else {
            PureScanner.scanTokens(line, onException = ::println).forEach {
                print("[${it.lexeme}] @ ${it.offset}")

                when (it) {
                    is Token.String -> println(" >> \"${it.value}\"")
                    is Token.Literal<*> -> println(" >> ${it.value}")
                    else -> println()
                }
            }

            println(
                AstPrinter.print(
                    Expr.Binary(
                        Expr.Unary(
                            Token.Minus(1),
                            Expr.Literal(Token.Number(1, 35555.0))
                        ), Token.Star(1), Expr.Grouping(
                            Expr.Literal(Token.Number(1, 45.67))
                        )
                    )
                )
            )
        }
    }
}
