package pbt.kotlin.presentation

import net.jqwik.api.*
import net.jqwik.api.constraints.CharRange

class ExhaustiveGenerationExamples {

    @Property
    fun allChessSquares(
        @ForAll column: @CharRange(from = 'a', to = 'h') Char,
        @ForAll row: @CharRange(from = '1', to = '8') Char
    ) {
        println("$column$row")
    }

    // Generates all 362880 mini sudokus of size 3x3
    @Property(generation = GenerationMode.EXHAUSTIVE)
    fun miniSudokus(@ForAll("sudokus") sudoku: List<List<Int>>) {
        println(sudoku.format())
    }

    @Provide
    fun sudokus() =
        Arbitraries.shuffle(1, 2, 3, 4, 5, 6, 7, 8, 9)
            .map { listOf(it.subList(0, 3), it.subList(3, 6), it.subList(6, 9)) }

    private fun List<List<Int>>.format() = String.format("%s%n%s%n%s%n", this[0], this[1], this[2])
}