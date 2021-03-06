package pbt.kotlin.jqwik

import net.jqwik.api.Disabled
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.constraints.Size
import org.assertj.core.api.Assertions.assertThat

class ReverseProperties {

    @Property
    fun `the size remains the same`(@ForAll list: List<Int>) =
        list.size == list.reversed().size

    @Property
    fun `reversing keeps all elements`(@ForAll list: List<Int>) {
        assertThat(list.reversed()).containsAll(list)
    }

    @Property
    @Disabled("Expected to fail")
    fun `reversing keeps the list unchanged`(@ForAll list: List<Int>) {
        assertThat(list.reversed()).isEqualTo(list)
    }

    @Property
    fun `reversing twice results in original list`(@ForAll list: List<Int>) {
        assertThat(list.reversed().reversed()).isEqualTo(list)
    }

    @Property
    fun `reversing swaps first and last`(@ForAll @Size(min=2) list: List<Int>) {
        val reversed = list.reversed()
        assertThat(reversed[0]).isEqualTo(list[list.size - 1])
        assertThat(reversed[list.size - 1]).isEqualTo(list[0])
    }
}