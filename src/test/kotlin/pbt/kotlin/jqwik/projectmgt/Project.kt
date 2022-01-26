package pbt.kotlin.jqwik.projectmgt

class Project @JvmOverloads constructor(name: String, membersLimit: Int = 10) {
    val name: String
    private val membersLimit: Int
    private val members: MutableSet<User> = HashSet()

    init {
        require(name.isNotBlank()) { "Project name must not be blank" }
        this.name = name
        this.membersLimit = membersLimit
    }

    fun addMember(newMember: User) {
        requireUnknownEmail(newMember)
        requireCapacityNotReached()
        members.add(newMember)
    }

    private fun requireCapacityNotReached() {
        require(members.size < membersLimit) {
            "Maximum number of $membersLimit members already reached"
        }
    }

    private fun requireUnknownEmail(newMember: User) {
        require(!emailKnown(newMember.email)) {
            "Member with email [${newMember.email}] already exists."
        }
    }

    fun isMember(user: User) = members.contains(user)

    private fun emailKnown(email: String): Boolean = members.any { m -> m.email == email }

}