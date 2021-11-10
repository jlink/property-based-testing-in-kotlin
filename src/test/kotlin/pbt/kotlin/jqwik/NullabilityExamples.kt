package pbt.kotlin.jqwik

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.PropertyDefaults
import net.jqwik.api.constraints.WithNull

@PropertyDefaults(tries = 10)
class NullabilityExamples {

    @Property
    fun `also generate nulls`(@ForAll nullOrString: String?) {
        println(nullOrString)
    }

    @Property(tries = 100)
    fun `embedded nulls are not recognized`(@ForAll list: List<String?>) {
        println(list)
    }

    @Property(tries = 100)
    fun `generate nulls in list`(@ForAll list: List<@WithNull String?>) {
        println(list)
    }

}