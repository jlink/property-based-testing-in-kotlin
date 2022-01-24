package pbt.kotlin.presentation

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import org.assertj.core.api.Assertions.assertThat

internal class SortingProperties {

    @Property
    //@Report(Reporting.GENERATED)
    fun <T : Comparable<T>> `sorting a list works`(@ForAll unsorted: List<T>) {
        val sorted = unsorted.sorted()
        assertThat(sorted.isSorted()).isTrue
    }

    private fun <T : Comparable<T>> List<T>.isSorted(): Boolean =
        if (size <= 1) true
        else this[0] <= this[1]
                && this.subList(1, size).isSorted()

}
