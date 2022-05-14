package pbt.kotlin.jqwik.projectmgt

class User(email: String) {
    val email: String

    init {
        assertValidEmail(email)

        // This is actually a bug because local part of an email address might be case-sensitive:
        this.email = email.lowercase()

        // this.email = email
    }

    private fun assertValidEmail(email: String) {
        val countAt = email.chars().filter { c: Int -> c == '@'.code }.count()
        require(countAt == 1L) { "Email <$email> should contain exactly one '@' sign" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other === null) return false
        if (other !is User) return false
        return email == other.email
    }

    override fun hashCode() = email.hashCode()

    override fun toString() = "User<$email>"

}