package pbt.kotlin.presentation.stack

import net.jqwik.api.Arbitraries.just
import net.jqwik.api.Arbitraries.oneOf
import net.jqwik.api.stateful.Action
import net.jqwik.kotlin.api.any
import org.assertj.core.api.Assertions.assertThat

object MyStackActions {

    fun actions() = oneOf(push(), clear(), pop())

    fun push() = String.any().alpha().ofLength(5).map { PushAction(it) }

    private fun clear() = just(ClearAction())

    private fun pop() = just(PopAction())

    class PushAction constructor(private val element: String) : Action<MyStack> {
        override fun run(state: MyStack): MyStack {
            val sizeBefore: Int = state.size
            state.push(element)
            assertThat(state.isEmpty).isFalse
            assertThat(state.size).isEqualTo(sizeBefore + 1)
            assertThat(state.top).isEqualTo(element)
            return state
        }

        override fun toString() = "push($element)"
    }

    class ClearAction : Action<MyStack> {
        override fun run(state: MyStack): MyStack {
            state.clear()
            assertThat(state.isEmpty).isTrue
            return state
        }

        override fun toString() = "clear"
    }

    class PopAction : Action<MyStack> {
        override fun precondition(state: MyStack) = !state.isEmpty

        override fun run(state: MyStack): MyStack {
            val sizeBefore: Int = state.size
            val topBefore: String = state.top
            val popped = state.pop()
            assertThat(popped).isEqualTo(topBefore)
            assertThat(state.size).isEqualTo(sizeBefore - 1)
            return state
        }

        override fun toString() = "pop"
    }
}