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
        require(!emailKnown(newMember.email)) {
            "Member with email [${newMember.email}] already exists."
        }
        if(members.size >= membersLimit) {
            val message = "Maximum number of $membersLimit members already reached"
            throw RuntimeException(message)
        }
        members.add(newMember)
    }

    fun isMember(user: User) = members.contains(user)

    private fun emailKnown(email: String): Boolean = members.any { m -> m.email == email }

}