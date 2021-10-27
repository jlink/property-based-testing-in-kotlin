package pbt.kotlin.jqwik

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.constraints.Size
import org.assertj.core.api.Assertions.assertThat

class ReverseProperties {

    @Property
    fun `reversing keeps all elements`(@ForAll list: List<Int>) {
        assertThat(list.reversed()).containsAll(list)
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