package pbt.kotlin.jqwik.projectmgt

import net.jqwik.api.*
import net.jqwik.api.constraints.NotBlank
import net.jqwik.api.constraints.NumericChars
import net.jqwik.api.constraints.Size
import net.jqwik.api.constraints.UniqueElements
import net.jqwik.web.api.Email
import net.jqwik.web.api.Web

class ProjectManagementProperties {

    @Property(tries = 100)
    fun `can add up to 10 team members to a project`(
        @ForAll projectName: @NotBlank @NumericChars String,
        @ForAll emails: @Size(max = 10) @UniqueElements List<@Email String>
    ) {
        val project = Project(projectName)
        val users = emails.map { User(it) }.toList()
        for (user in users) {
            project.addMember(user)
        }
        for (user in users) {
            project.isMember(user)
        }
    }

    @Property(tries = 100)
    fun `can add team members up to specified limit`(@ForAll("members") users: List<User>) {
        val limit = users.size
        val project = Project("projectName", limit)
        for (user in users) {
            project.addMember(user)
        }
        for (user in users) {
            project.isMember(user)
        }
    }

    @Provide
    fun members(): Arbitrary<List<User>> {
        return Web.emails().map { User(it) }.list().ofMaxSize(50).uniqueElements()
    }

}