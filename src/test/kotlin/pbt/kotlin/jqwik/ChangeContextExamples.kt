package pbt.kotlin.jqwik

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.Reporter

class ChangeContextExamples {

    val key = "a number"

    @Property(tries = 10)
    fun Reporter.reportGeneratedNumbers(@ForAll aNumber: Int) {
        publishReport(key, aNumber)
    }
}