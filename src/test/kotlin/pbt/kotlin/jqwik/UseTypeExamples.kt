package pbt.kotlin.jqwik

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.constraints.AlphaChars
import net.jqwik.api.constraints.NotBlank
import net.jqwik.api.constraints.UseType
import net.jqwik.web.api.Email

class UseTypeExamples {

    @Property(tries = 20)
    fun generateCommunications(@ForAll @UseType communication: Communication) {
        println(communication)
    }
}

data class Communication(val from: User, val to: User)

data class User(val identity: Person, @Email val email: String)

data class Person(val firstName: String?, @AlphaChars @NotBlank val lastName: String)

//data class Name(@AlphaChars val value: String) {
//    init {
//        require(value.length > 1)
//    }
//    override fun toString() = value
//}

// typealias MyName=@AlphaChars @StringLength(min = 5) String
