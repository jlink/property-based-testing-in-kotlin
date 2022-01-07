package pbt.kotlin.presentation

import net.jqwik.api.*
import net.jqwik.api.constraints.Positive
import net.jqwik.kotlin.api.any
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage
import kotlin.math.sqrt

class ConstrainingValuesExamples {

    // Expected to fail since there is no sqrt of negative numbers
    // @Property
    fun `square of root is original value`(@ForAll aNumber: Double) {
        val sqrt = sqrt(aNumber)
        assertThat(sqrt * sqrt)
            .isCloseTo(aNumber, Percentage.withPercentage(10.0))
    }


    // Use annotation to constrain generated numbers
    @Property
    fun `square of root is original value 1`(@ForAll aNumber: @Positive Double) {
        val sqrt = sqrt(aNumber)
        assertThat(sqrt * sqrt)
            .isCloseTo(aNumber, Percentage.withPercentage(10.0))
    }

    // Provide arbitrary through builder methods
    @Property
    fun `square of root is original value 2`(@ForAll("positiveDoubles") aNumber: Double) {
        val sqrt = sqrt(aNumber)
        assertThat(sqrt * sqrt)
            .isCloseTo(aNumber, Percentage.withPercentage(10.0))
    }

    @Provide
    fun positiveDoubles() = Double.any(0.0..Double.MAX_VALUE)


    // Use assumption to constrain execution of checks
    @Property
    fun `square of root is original value 3`(@ForAll aNumber: Double) {
        Assume.that(aNumber > 0)
        val sqrt = Math.sqrt(aNumber)
        assertThat(sqrt * sqrt).isCloseTo(aNumber, Percentage.withPercentage(10.0))
    }

}