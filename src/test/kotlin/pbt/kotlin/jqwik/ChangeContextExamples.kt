package pbt.kotlin.jqwik

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.PropertyDefaults
import net.jqwik.api.Reporter

@PropertyDefaults(tries = 10)
class ChangeContextExamples {

    val key = "a number"

    @Property
    // Reporter provides access to JUnitPlatform's reporting mechanism
    fun reportGeneratedNumbers(@ForAll aNumber: Int, reporter: Reporter) {
        reporter.publishReport(key, aNumber)
    }

    @Property
    fun Reporter.reportGeneratedNumbers(@ForAll aNumber: Int) {
        publishReport(key, aNumber)
    }
}