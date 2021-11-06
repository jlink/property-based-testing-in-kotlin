package pbt.kotlin.jqwik

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.PropertyDefaults
import net.jqwik.api.Provide
import net.jqwik.api.constraints.AlphaChars
import net.jqwik.api.constraints.StringLength
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.ofLength

@PropertyDefaults(tries = 10)
class ConfigurationExamples {

    @Property
    fun testAlphaStrings(@ForAll @StringLength(min = 2, max = 42) @AlphaChars string: String) {
        println(string)
    }

    @Property
    fun testAsciiStrings(@ForAll("asciiOnly") string: String) {
        println(string)
    }

    @Provide
    fun asciiOnly() = String.any().ascii().ofLength(2..42)
}