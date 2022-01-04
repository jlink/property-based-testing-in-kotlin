package pbt.kotlin.jupiter

import org.junit.jupiter.api.Test

class KotlinParameterBug {

    suspend fun String.aFunction(anInt: Int) {}

    @Test
    fun test() {
        val methods = KotlinParameterBug::class.java.declaredMethods
        val parametersOfAFunction = methods.first { m -> m.name == "aFunction" }.parameters
        val intParameter = parametersOfAFunction[1]

        println("type= " + intParameter.type)
        println("annotatedType= " + intParameter.annotatedType.type)

        assert(intParameter.type == intParameter.annotatedType.type)
    }
}