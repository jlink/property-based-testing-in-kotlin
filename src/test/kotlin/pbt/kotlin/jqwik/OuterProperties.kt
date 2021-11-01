package pbt.kotlin.jqwik

import net.jqwik.api.Group
import net.jqwik.api.Property

class OuterProperties {

    @Property
    fun outerProp() {}

    @Group
    inner class InnerProperties {
        @Property
        fun innerProp() {}
    }
}