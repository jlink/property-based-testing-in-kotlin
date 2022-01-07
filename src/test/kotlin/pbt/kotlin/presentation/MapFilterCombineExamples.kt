package pbt.kotlin.presentation

import net.jqwik.api.*
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.combine
import net.jqwik.kotlin.api.ofLength
import java.util.*


class MapFilterCombineExamples {
    @Property
    fun evenNumbersAreEven(@ForAll("evenUpTo10000") anInt: Int): Boolean {
        return anInt % 2 == 0
    }

    @Provide
    fun evenUpTo10000() = Int.any(1..10000).filter { i -> i % 2 == 0 }

    @Provide
    fun even() = Int.any().filter { i: Int -> i % 2 == 0 }

    @Provide
    fun _evenUpTo10000() = Int.any(1..5000).map { i -> i * 2 }

    @Provide
    fun hexNumbers() =
        Int.any(1..0xFFFF)
            .map { it.toString(0xF) }

    @Provide
    fun _hexNumbers() =
        Int.any(1..0xFFFF)
            .map(Integer::toHexString)

    @Property(tries = 50)
    @Report(Reporting.GENERATED)
    fun anyValidPersonHasAFullName(@ForAll("valid people") aPerson: Person): Boolean {
        return aPerson.fullName().length >= 5
    }

    @Provide
    fun `valid people`(): Arbitrary<Person> {
        val firstName = String.any().withCharRange('a', 'z')
            .ofLength(2..10)
            .map { it.capitalize() }
        val lastName = String.any().withCharRange('a', 'z')
            .ofLength(2..20)
        return combine(firstName, lastName) { first, last -> Person(first, last) }
    }

    private fun String.capitalize() = this.substring(0, 1).uppercase(Locale.getDefault()) + this.substring(1)

    data class Person(val firstName: String, val lastName: String) {
        fun fullName() = "$firstName $lastName"
    }

}