package pbt.kotlin.jqwik

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import org.assertj.core.api.Assertions.assertThat

class KotlinFunctionProperties {

    @Property(tries = 10)
    fun `all generated functions return a String`(
        @ForAll func: ((Int, Int) -> String),
        @ForAll int1: Int,
        @ForAll int2: Int
    ) {
        assertThat(func(int1, int2)).isInstanceOf(String::class.java)
        println(func(int1, int2))
        println(func(int1, int2))
    }
}