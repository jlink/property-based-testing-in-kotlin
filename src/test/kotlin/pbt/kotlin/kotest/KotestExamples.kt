package pbt.kotlin.kotest

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import io.kotest.property.forAll
import org.assertj.core.api.Assertions.assertThat

class KotestExamples : StringSpec({


    "shrink int values" {
        forAll(Arb.int(1000..1_000_000)) { anInt ->
            // Should shrink to 90000 but does not most of the time
            // anInt < 90000
            true
        }
    }

    "reversing keeps all elements" {
        checkAll<List<Int>> { list ->
            assertThat(list.reversed()).containsAll(list)
        }
    }

    "reversing twice results in original list" {
        checkAll<List<Int>> { list ->
            assertThat(list.reversed().reversed()).isEqualTo(list)
        }
    }

    "reversing swaps first and last" {
        checkAll(Arb.list(Arb.int(), 1..100)) { list ->
            val reversed = list.reversed()
            assertThat(reversed[0]).isEqualTo(list[list.size - 1])
            assertThat(reversed[list.size - 1]).isEqualTo(list[0])
        }
    }
})
