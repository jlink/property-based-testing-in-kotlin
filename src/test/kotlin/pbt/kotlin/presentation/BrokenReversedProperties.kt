package pbt.kotlin.presentation

import net.jqwik.api.*

class BrokenReversedProperties {

    fun <T> List<T>.brokenReversed(): List<T> =
        if (this.size <= 3) this.reversed() else this


    @Property
    fun `reversing swaps first and last`(@ForAll list: List<Int>): Boolean {
        Assume.that(list.isNotEmpty())
        val reversed = list.brokenReversed()
        return reversed[0] == list[list.size - 1]
    }

}