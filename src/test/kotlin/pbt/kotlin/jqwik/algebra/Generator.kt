@file:Suppress("ClassName")

package pbt.kotlin.jqwik.algebra

import kotlin.random.Random

fun interface Generator<T> {
    operator fun invoke(random: Random): T
}

class naturals(val max: Int) : Generator<Int> {
    override fun invoke(random: Random) = random.nextInt(0, max + 1)

    override fun toString() = "naturals(0..$max)"
}

class just<T>(val value: T) : Generator<T> {
    override fun invoke(random: Random) = value

    override fun toString() = "just($value)"
}

class FlatMap<T, U>(val base: Generator<T>, val mapper: (T) -> (Generator<U>)) : Generator<U> {
    override fun invoke(random: Random) = mapper(base(random))(random)
    override fun toString() = "flatMap($base with $mapper)"
}

class list<T>(val elements: Generator<T>, val size: Int) : Generator<List<T>> {
    override fun invoke(random: Random): List<T> {
        val list = mutableListOf<T>()
        repeat(size) {
            list.add(elements(random))
        }
        return list
    }

    override fun toString() = "list<$elements>($size)"
}

class map<T, U>(val base: Generator<T>, val mapper: (T) -> U): Generator<U> {
    override fun invoke(random: Random): U = mapper(base(random))
}

fun <T, U> flatMap(base: Generator<T>, mapper: (T) -> (Generator<U>)): Generator<U> {
    val flatMapped = FlatMap(base, mapper)
    if (base is just) {
        val simplified = mapper(base.value)
        println("simplifying $flatMapped -> $simplified")
        return simplified
    } else {
        return flatMapped
    }
}

