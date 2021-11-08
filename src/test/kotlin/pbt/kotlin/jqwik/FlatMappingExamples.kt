package pbt.kotlin.jqwik

import net.jqwik.api.*
import net.jqwik.api.constraints.Size
import net.jqwik.api.constraints.UniqueElements
import net.jqwik.kotlin.api.JqwikIntRange
import net.jqwik.kotlin.api.any
import org.assertj.core.api.Assertions.assertThat

class FlatMappingExamples {

    @Disabled("This property fails because index does not consider the number of elements in list")
    @Property
    fun `index works for element in list`(@ForAll list: List<Int>, @ForAll index: Int) {
        val element = list[index]
        assertThat(list.indexOf(element)).isEqualTo(index)
    }

    @Property
    fun `index works for element in list - using assumption`(
        @ForAll @UniqueElements @Size(max = 50) list: List<Int>,
        @ForAll @JqwikIntRange(max = 49) index: Int
    ) {
        Assume.that(index >= 0 && index < list.size)
        val element = list[index]
        assertThat(list.indexOf(element)).isEqualTo(index)
    }

    @Property
    fun `index works for element in list`(@ForAll("listWithValidIndex") listWithIndex: Pair<List<Int>, Int>) {
        val (list, index) = listWithIndex
        val element = list[index]
        assertThat(list.indexOf(element)).isEqualTo(index)
    }

    @Provide
    fun listWithValidIndex(): Arbitrary<Pair<List<Int>, Int>> {
        val lists = Int.any().list().uniqueElements().ofMinSize(1)
        return lists.flatMap { list -> Int.any(0 until list.size).map { index -> Pair(list, index) } }
    }

}