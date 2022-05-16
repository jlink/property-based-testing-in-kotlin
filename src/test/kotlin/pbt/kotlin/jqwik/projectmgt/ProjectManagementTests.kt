package pbt.kotlin.jqwik.projectmgt

import net.jqwik.api.Example

class ProjectManagementTests {

    @Example
    fun `you can add many team members`() {
        val project = Project("My big project")
        val alex = User("alex@example.com")
        val kim = User("kim@example.com")
        val pat = User("pat@example.com")
        project.addMember(alex)
        project.addMember(kim)
        project.addMember(pat)
        assert(project.isMember(alex))
        assert(project.isMember(kim))
        assert(project.isMember(pat))
    }

}