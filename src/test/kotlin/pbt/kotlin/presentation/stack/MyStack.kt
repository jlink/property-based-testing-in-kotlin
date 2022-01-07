package pbt.kotlin.presentation.stack

class MyStack {

    private val elements: MutableList<String> = mutableListOf()

    fun push(element: String): Unit {
        elements.add(0, element)
    }

    fun pop(): String {
        return elements.removeAt(0)
    }

    fun clear(): Unit {
        //Uncomment to see property fail:
        //if (elements.size < 3)
        elements.clear()
    }

    val isEmpty: Boolean
        get() = elements.isEmpty()

    val size: Int
        get() = elements.size

    val top: String
        get() = elements[0]

    override fun toString() = elements.toString()

    override fun hashCode() = elements.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other !is MyStack) return false
        return elements == other.elements
    }
}