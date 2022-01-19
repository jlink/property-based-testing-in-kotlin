package pbt.kotlin.jqwik.algebra

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import org.assertj.core.api.Assertions.assertThat
import kotlin.random.Random

class GeneratorProperties {


    @Property
    fun equivalence(@ForAll randomSeed: Long) {

        val gen1 = list(
            naturals(99),
            5
        )

        val sizes = just(5)
        val gen2 = flatMap(sizes) { size ->
            list(
                flatMap(just(99)) { max -> naturals(max) },
                size
            )
        }

        val random1 = Random(randomSeed)
        val random2 = Random(randomSeed)

        assertThat(gen1(random1)).isEqualTo(gen2(random2))
    }

}

