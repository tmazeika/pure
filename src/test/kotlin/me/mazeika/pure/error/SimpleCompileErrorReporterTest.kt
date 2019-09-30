package me.mazeika.pure.error

import io.kotlintest.properties.Gen
import io.kotlintest.properties.assertAll
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec

class SimpleCompileErrorReporterTest : StringSpec({
    "constructor should not write anything" {
        val out = StringBuilder()
        SimpleCompileErrorReporter(out)

        out.toString() shouldBe ""
    }

    "report should require a positive row" {
        val reporter = SimpleCompileErrorReporter(StringBuilder())

        assertAll(
            Gen.int().filter { it <= 0 },
            Gen.int()
        )
        { row: Int, column: Int ->
            shouldThrow<IllegalArgumentException> {
                reporter.report("Hello, world!", "code.p", row, column)
            }
        }
    }

    "report should require a positive column" {
        val reporter = SimpleCompileErrorReporter(StringBuilder())

        assertAll(
            Gen.int(),
            Gen.int().filter { it <= 0 })
        { row: Int, column: Int ->
            shouldThrow<IllegalArgumentException> {
                reporter.report("Hello, world!", "code.p", row, column)
            }
        }
    }

    "report should print normally" {
        assertAll(
            Gen.string(),
            Gen.string(),
            Gen.positiveIntegers(),
            Gen.positiveIntegers()
        )
        { message: String,
          sourceFile: String,
          sourceRow: Int,
          sourceColumn: Int ->
            val out = StringBuilder()
            val reporter = SimpleCompileErrorReporter(out)

            reporter.report(message, sourceFile, sourceRow, sourceColumn)

            out.toString() shouldBe
                    "Error at $sourceFile:$sourceRow:$sourceColumn\n$message\n"
        }
    }
})
