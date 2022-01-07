package pbt.kotlin.presentation.stack

import net.jqwik.api.*
import net.jqwik.api.stateful.ActionSequence
import net.jqwik.api.stateful.Invariant
import org.assertj.core.api.Assertions.assertThat

class MyStackProperties {

    @Property
    fun `check my stack`(@ForAll("sequences") sequence: ActionSequence<MyStack>) =
        sequence.run(MyStack())

    @Provide
    fun sequences() = Arbitraries.sequences(MyStackActions.actions())

    @Property(tries = 10)
    fun `check my string stack with invariant`(@ForAll("sequences") sequence: ActionSequence<MyStack>) {
        val sizeNeverNegative: Invariant<MyStack> = Invariant { assertThat(it.size).isGreaterThanOrEqualTo(0) }
        sequence
            .withInvariant(sizeNeverNegative)
            .run(MyStack())
    }

    @Property
    fun `are equal after same sequence of pushes`(@ForAll("pushes") sequence: ActionSequence<MyStack>): Boolean {
        val stack1 = sequence.run(MyStack())
        val stack2 = sequence.run(MyStack())
        return stack1 == stack2
    }

    @Provide
    fun pushes() = Arbitraries.sequences(MyStackActions.push())
}