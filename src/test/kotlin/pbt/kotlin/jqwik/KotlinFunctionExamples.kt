package pbt.kotlin.jqwik

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.constraints.Size
import org.assertj.core.api.Assertions.assertThat

class KotlinFunctionExamples {

    @Property(tries = 10)
    fun `all generated functions return a String`(
        @ForAll func: ((Int, Int) -> String),
        @ForAll int1: Int,
        @ForAll int2: Int
    ) {
        assertThat(func(int1, int2)).isInstanceOf(String::class.java)
        println("f($int1, $int2) = ${func(int1, int2)}")
        println("f($int1, $int2) = ${func(int1, int2)}")
        println("f($int2, $int1) = ${func(int2, int1)}")
    }
}