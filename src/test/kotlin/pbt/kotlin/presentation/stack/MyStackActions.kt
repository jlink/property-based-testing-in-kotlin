package pbt.kotlin.presentation.stack

import net.jqwik.api.Arbitraries.just
import net.jqwik.api.Arbitraries.oneOf
import net.jqwik.api.Arbitrary
import net.jqwik.api.stateful.Action
import net.jqwik.kotlin.api.any
import org.assertj.core.api.Assertions.assertThat
import java.io.Serializable

object MyStackActions {

    fun actions(): Arbitrary<Action<MyStack>> {
        return oneOf(push(), clear(), pop())
    }

    fun push(): Arbitrary<Action<MyStack>> {
        return String.any().alpha().ofLength(5).map { PushAction(it) }
    }

    private fun clear(): Arbitrary<Action<MyStack>> {
        return just(ClearAction())
    }

    private fun pop(): Arbitrary<Action<MyStack>> {
        return just(PopAction())
    }

    private class PushAction constructor(private val element: String) : Action<MyStack>, Serializable {
        override fun run(model: MyStack): MyStack {
            val sizeBefore: Int = model.size
            model.push(element)
            assertThat(model.isEmpty).isFalse
            assertThat(model.size).isEqualTo(sizeBefore + 1)
            assertThat(model.top).isEqualTo(element)
            return model
        }

        override fun toString() = "push($element)"
    }

    private class ClearAction : Action<MyStack>, Serializable {
        override fun run(model: MyStack): MyStack {
            model.clear()
            assertThat(model.isEmpty).isTrue
            return model
        }

        override fun toString() = "clear"
    }

    private class PopAction : Action<MyStack>, Serializable {
        override fun precondition(model: MyStack): Boolean {
            return !model.isEmpty
        }

        override fun run(model: MyStack): MyStack {
            val sizeBefore: Int = model.size
            val topBefore: String = model.top
            val popped = model.pop()
            assertThat(popped).isEqualTo(topBefore)
            assertThat(model.size).isEqualTo(sizeBefore - 1)
            return model
        }

        override fun toString() = "pop"
    }
}