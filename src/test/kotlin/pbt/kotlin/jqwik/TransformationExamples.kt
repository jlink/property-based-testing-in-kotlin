package pbt.kotlin.jqwik

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.Provide
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.array
import java.util.*

class TransformationExamples {

    @Property
    fun evenNumbersByFiltering(@ForAll("filteredEvens") anInt: Int) {
        println(anInt)
    }

    @Provide
    fun filteredEvens() = Int.any(1..100000).filter { it % 2 == 0 }

    @Property
    fun evenNumbersByMapping(@ForAll("mappedEvens") anInt: Int) {
        println(anInt)
    }

    @Provide
    fun mappedEvens() = Int.any(1..50000).map { it * 2}

    @Property
    fun hexValues(@ForAll("hexNumbers") aHex: String) {
        println(aHex)
    }

    @Provide
    fun hexNumbers() = Int.any(1..Int.MAX_VALUE).map { it.toString(16)}

    @Property
    fun listOf10Doubles(@ForAll("listOfDoubles") list: List<Double>) {
        println(list)
    }

    @Provide
    fun listOfDoubles() = Double.any().list().ofSize(10)

    @Property
    fun arrayOf10Doubles(@ForAll("arrayOfDoubles") array: Array<Double>) {
        println(array.contentToString())
    }

    @Provide
    fun arrayOfDoubles() = Double.any().array<Double, Array<Double>>()
    //fun arrayOfDoubles() = Double.any().array(Array<Double>::class.java).ofSize(10)
}