package pbt.kotlin.jqwik.projectmgt

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.Provide
import net.jqwik.api.constraints.NotBlank
import net.jqwik.api.constraints.Size
import net.jqwik.api.constraints.UniqueElements
import net.jqwik.web.api.Email
import net.jqwik.web.api.Web

class ProjectManagementProperties {

    // This property sometimes detects a bug in User.kt
    @Property(tries = 100)
    fun `can add up to 10 team members to a project`(
        @ForAll projectName: @NotBlank String,
        @ForAll emails: @Size(max = 10) @UniqueElements List<@Email String>
    ) {
        val project = Project(projectName)
        val users = emails.map { User(it) }.toList()
        for (user in users) {
            project.addMember(user)
        }
        for (user in users) {
            assert(project.isMember(user))
        }
    }

    @Property(tries = 100)
    fun `can add team members up to specified limit`(@ForAll("members") users: @Size(max = 50) List<User>) {
        val limit = users.size
        val project = Project("projectName", limit)
        for (user in users) {
            project.addMember(user)
        }
        for (user in users) {
            assert(project.isMember(user))
        }
    }

    @Provide
    fun members() = Web.emails().map { User(it) }.list().uniqueElements()

}