package pbt.kotlin.jqwik

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.PropertyDefaults
import net.jqwik.api.constraints.*
import net.jqwik.kotlin.api.JqwikIntRange
import java.math.BigDecimal

@PropertyDefaults(tries = 10)
class PlainJqwikTests {

    @Property
    fun aProperty(@ForAll @AlphaChars aString: String) {
        //println(aString)
    }

    @Property
    fun anotherProperty(@ForAll anInt: Int) {
        //println(anInt)
    }

    @Property(tries = 30)
    fun propertyWithAnnotatedParameterTypes(@ForAll list: @Size(5) List<@StringLength(3) @NumericChars String>) {
        //println(list)
    }

    @Property(tries = 30)
    fun propertyWithNullableParameter(@ForAll nullOrString: String?) {
        //println(nullOrString)
    }

    @Property(tries = 30)
    fun propertyWithNullableParameterType(@ForAll list: List<@WithNull(0.5) Int>) {
        //println(list)
    }

    @Property
    fun `property with a long name`(@ForAll aNumber: BigDecimal) {
    }

    @Property
    fun `positive is above 0`(@ForAll @Positive anInt: Int) = anInt > 0

    @Property
    fun `negative is below 0`(@ForAll @Negative anInt: Int) {
        assert(anInt < 0, { "$anInt should be < 0" })
    }
}