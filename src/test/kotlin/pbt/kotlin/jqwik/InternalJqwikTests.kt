package pbt.kotlin.jqwik

import net.jqwik.api.ForAll
import net.jqwik.api.Label
import net.jqwik.api.Property
import net.jqwik.api.constraints.AlphaChars
import java.math.BigDecimal

internal class InternalJqwikTests {

    @Property
    @Label("aProperty")
    internal fun aProperty(@ForAll @AlphaChars aString: String) {
        //println(aString)
    }

    @Property
    internal fun anotherProperty(@ForAll anInt: Int) {
        //println(anInt)
    }

    @Property(tries = 25)
    internal fun `internal property with a very long name`(@ForAll aNumber: BigDecimal) {
        //println(aNumber)
    }
}