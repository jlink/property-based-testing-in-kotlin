package pbt.kotlin.jupiter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ReverseExampleTests {

    @Test
    fun `any list with elements can be reversed`() {
       val original : List<Int> = listOf(1, 2, 3)
       assertThat(original.reversed()).containsExactly(3, 2, 1)
    }
}