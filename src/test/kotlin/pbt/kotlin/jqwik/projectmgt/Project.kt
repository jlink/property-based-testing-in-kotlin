package pbt.kotlin.jqwik.projectmgt

class Project constructor(name: String, membersLimit: Int = 10) {
    val name: String
    private val membersLimit: Int
    private val members: MutableSet<User> = HashSet()

    init {
        require(name.isNotBlank()) { "Project name must not be blank" }
        this.name = name
        this.membersLimit = membersLimit
    }

    fun addMember(newMember: User) {
        requireCapacityNotReached()
        requireUnknownEmail(newMember)
        members.add(newMember)
    }

    private fun requireCapacityNotReached() {
        if (members.size >= membersLimit) {
            val message = "Maximum number of $membersLimit members already reached"
            throw RuntimeException(message)
        }
    }

    private fun requireUnknownEmail(newMember: User) {
        require(!emailKnown(newMember.email)) {
            "Member with email [${newMember.email}] already exists."
        }
    }

    fun isMember(user: User) = members.contains(user)

    private fun emailKnown(email: String): Boolean = members.any { m -> m.email == email }

    override fun toString() = "Project<$name>"
}