package pbt.kotlin.jqwik

import net.jqwik.api.ForAll
import net.jqwik.api.Property

class MetamorphicExamples {

    fun sumUp(vararg numbers: Int) = numbers.sum()

    @Property
    fun `sum is enhanced by X`(@ForAll list: IntArray, @ForAll x: Int) : Boolean {
        val original = sumUp(*list)
        val sum = sumUp(*list, x)
        return sum == original + x
    }
}