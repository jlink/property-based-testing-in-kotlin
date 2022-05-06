package pbt.kotlin.jqwik

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.constraints.AlphaChars
import net.jqwik.api.constraints.NotBlank
import net.jqwik.api.constraints.UseType
import net.jqwik.web.api.Email

class UseTypeWithDataclassesExamples {

    @Property(tries = 20)
    fun generateCommunications(@ForAll @UseType communication: Communication) {
        println(communication)
    }
}

data class Person(val firstName: String?, @NotBlank val lastName: String)

data class User(val identity: Person, @Email val email: String)

data class Communication(val from: User, val to: User)

//data class Name(@AlphaChars val value: String) {
//    init {
//        require(value.length > 5)
//    }
//    override fun toString() = value
//}