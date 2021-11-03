# Property-based Testing in Kotlin

Kotlin is currently the most hyped language on the JVM. With good reason.
Kotlin is mostly compatible with Java and comes with a nice set of features 
to make programming on the JVM less burdensome and more functional.

However, there is not much coverage of _Property-based Testing_ (PBT) focusing on Kotlin.
Here are some of the few articles and resources I found:

- [A short blog series](https://medium.com/default-to-open/property-based-testing-in-kotlin-part-1-56929927b8b8)
- [A chapter of "The Joy of Kotlin"](https://livebook.manning.com/book/the-joy-of-kotlin/b-property-based-testing-in-kotlin/v-8/156)

This article covers the application of PBT in Kotlin using [jqwik's](https://jqwik.net)
Kotlin module.

### The Code

You can find [all code examples on github](https://github.com/jlink/property-based-testing-in-kotlin/tree/master/src).

<!-- Generated toc must be stripped of `nbsp` occurrences in links -->
<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
## Table of Contents  

- [A Short Intro to PBT](#a-short-intro-to-pbt)
- [Jqwik, JUnit Platform and Kotlin](#jqwik-junit-platform-and-kotlin)
- [Setting Up Jqwik](#setting-up-jqwik)
- [Special Kotlin Support](#special-kotlin-support)
  - [Compatibility](#compatibility)
  - [Nullability](#nullability)
  - [Convenience Methods](#convenience-methods)
  - [Testing of Asynchronous Code](#testing-of-asynchronous-code)
  - [Quirks](#quirks)
- [Alternatives to jqwik](#alternatives-to-jqwik)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## A Short Intro to PBT

You all know example-based tests, e.g. using JUnit Jupiter:

```kotlin
@Test
fun `any list with elements can be reversed`() {
   val original : List<Int> = listOf(1, 2, 3)
   assertThat(original.reversed()).containsExactly(3, 2, 1)
}
```

One of the problems with example-based tests is that they typically promise more than they can keep.
Whereas they usually cover only a few examples, they want to make sure 
that the code works for _all valid input_.

Property-based tests, however, focus on common _properties_ (hence the name) of the code under test.
While it's not straightforward to predict the outcome of any list reversal without re-implementing reverse first,
there are a few things that should hold nonetheless:

- Reversing does not change the number of elements, nor the elements themselves
- Reversing twice will recreate the original list
- Reversing will swap the first element with the last, if there are at least two elements in the list

Here's how you could translate that into corresponding property-based tests using jqwik:

```kotlin
@Property
fun `reversing keeps all elements`(@ForAll list: List<Int>) {
    assertThat(list.reversed()).containsAll(list)
}

@Property
fun `reversing twice results in original list`(@ForAll list: List<Int>) {
    assertThat(list.reversed().reversed()).isEqualTo(list)
}

@Property
fun `reversing swaps first and last`(@ForAll @Size(min=2) list: List<Int>) {
    val reversed = list.reversed()
    assertThat(reversed[0]).isEqualTo(list[list.size - 1])
    assertThat(reversed[list.size - 1]).isEqualTo(list[0])
}
```

To be frank, the _Reverse List_ example is probably the most common and also the most boring one. 
If this is your first encounter with PBT, you should definitely get more motivation from other stories.
Here are some articles, two of which from myself:

- [In praise of property-based testing](https://increment.com/testing/in-praise-of-property-based-testing/)
- [Know for Sure](https://blogs.oracle.com/javamagazine/post/know-for-sure-with-property-based-testing)
- [How to Specify it. In Java!](https://johanneslink.net/how-to-specify-it/)


## Jqwik, JUnit Platform and Kotlin

As you have seen above _jqwik_ follows JUnit's lead in using an annotation (`@Property`) 
to mark plain functions as executable property. 
This is not as common in the Kotlin world as it is in Java, but you all know it from years of JUnit anyway.

Another thing that jqwik does for you is using the types of parameters annotated with `@ForAll`
to select an appropriate _generator_ for this parameter.
For example, the parameter `@ForAll aList: List<Int>` will generate lists of `Int` objects for you.
Many generators come with associated configurator annotations to further restrict and influence them.

Let's look at a more complex property combining all of that:

```kotlin
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
```

In this example you can see that configuration annotations can also be added to type parameters.
This property also changes the number of _tries_, which is 1000 by default, to 100.

_jqwik_ is not an independent library, but it comes as a _test engine_ for the 
[JUnit platform](https://junit.org/junit5/docs/current/user-guide/#overview-what-is-junit-5).
Despite its original focus on Java, jqwik has worked well with Kotlin for a long time.
As of version `1.6.0` there's an additional Kotlin module which makes the experience even smoother.

## Setting Up Jqwik

If you're already using JUnit 5, the set up for jqwik is really easy: 
Just add a dependency to `jqwik.net:jqwik-kotlin:<version>`. 
In practice I recommend to add a few compiler options to make your life easier.
Here's how you can do that in a Gradle Kotlin-based build file:

```
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf(
		  "-Xjsr305=strict", // Strict interpretation of nullability annotations in jqwik API
		  "-Xemit-jvm-type-annotations" // Enable nnotations on type variables
		)
        jvmTarget = "16" // 1.8 or above
        javaParameters = true // Get correct parameter names in jqwik reporting
    }
}
```

See 
[jqwik-starter-gradle-kotlin](https://github.com/jlink/jqwik-samples/tree/main/jqwik-starter-gradle-kotlin) 
and
[jqwik-starter-maven-kotlin](https://github.com/jlink/jqwik-samples/tree/main/jqwik-starter-maven-kotlin)
for fully configured examples for Gradle and Maven.

## Special Kotlin Support

The kotlin module has two main goals: 
Smoothing off some rough edges when using the jqwik API in Kotlin 
and supporting (some of) the additional features that Kotlin brings to the table.

### Compatibility

_jqwik_ is compatible with Kotlin's syntax and its somewhat different view on Java's type system. 
That means that:
- You can use functions instead of methods; functions returning `false` indicate a failure.
- Kotlin allows function names - and even class names! - with spaces and a few other special characters;
  you have to use backticks for those names, though.
- You can use Kotlin's expression syntax for property functions, 
  which allows for very concise statements:
  
  ```kotlin
  @Property 
  fun `positive is above 0`(@ForAll @Positive anInt: Int) = anInt > 0
  ```

- You can use all of Kotlin's array and collection types (and have them generated for you).
- You can use Kotlin function types instead of Java's lambdas and `@Functional` types.
  Thus, the following property works without the need for further configuration:

  ```kotlin
  @Property
  fun `all generated functions return a String`(
    @ForAll func: ((Int, Int) -> String),
    @ForAll int1: Int,
    @ForAll int2: Int
  ) {
    assertThat(func(int1, int2)).isInstanceOf(String::class.java)
  }
  ```

- You can use `internal` container classes and property methods.
- You can use Kotlin-style annotations and enums.
- You can use the different Kotlin constructs (data classes, sealed classes, type aliases etc.)
  as target types for generation or as test containers where it makes sense.
  As a special trick you can use singleton objects as container classes:

  ```kotlin
  object MySingletonProperties {
    @Property
    fun prop1() {}

    @Property
    fun prop2() {}
  }
  ```
  
  In this case the lifecycle changes in so far that all invocations of property methods
  will share them same singleton instance as `this` reference, 
  which is similar to Jupiter's `TestInstance.Lifecycle.PER_CLASS`.
  Sadly, IntelliJ does not recognize Kotlin's `object` definitions as test container classes
  in the editor.
  Running them through the project view, however, works as expected.

- You can use inner classes to nest test containers. 
  This requires the `@Group` annotation and also the modifier `inner`

  ```kotlin
  class OuterProperties {
    @Property
    fun outerProp() {}

    @Group
    inner class InnerProperties {
        @Property
        fun innerProp() {}
    }
  }
  ```

### Nullability

### Convenience Methods

### Testing of Asynchronous Code

### Constraints and Quirks

Some things are not as smooth (yet) as I'd like them to be.
For example: Kotlin's unsigned integer types, as well as inline classes, 
do not show up on Java's byte code side _at all_. 
That means they are treated naively;
`UInt` as `Int` and so on, an inlined class as the type it is inlining.
This means that you cannot write generators for these types, only for their Java counterpart.

Kotlin's support for injecting the correct annotations in the byte code is not perfect yet.
If you notice that a constraint annotation is sometimes not honoured by the generator,
this might be an open Kotlin bug.

## Alternatives to jqwik
