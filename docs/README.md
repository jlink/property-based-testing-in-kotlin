# Property-based Testing in Kotlin

Kotlin is currently the most hyped language on the JVM. With good reason.
Kotlin is mostly compatible with Java and comes with a nice set of features 
to make programming on the JVM less burdensome and more functional.

However, there is not much coverage of _Property-based Testing_ (PBT) in Kotlin.
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

- [A Very Short Intro to PBT](#a-very-short-intro-to-pbt)
- [Jqwik, JUnit Platform and Kotlin](#jqwik-junit-platform-and-kotlin)
- [Setting Up Jqwik](#setting-up-jqwik)
  - [Gradle](#gradle)
  - [Maven](#maven)
- [Jqwik's Kotlin Support](#jqwiks-kotlin-support)
- [Alternatives to jqwik](#alternatives-to-jqwik)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## A Very Short Intro to PBT

You all know example-based tests, e.g. using JUnit Jupiter:

```Kotlin
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
Here are some better ones:

- [In praise of property-based testing](https://increment.com/testing/in-praise-of-property-based-testing/)
- [Know for Sure](https://blogs.oracle.com/javamagazine/post/know-for-sure-with-property-based-testing)
- [How to Specify it. In Java!](https://johanneslink.net/how-to-specify-it/)


## Jqwik, JUnit Platform and Kotlin

As you have seen above _jqwik_ follows JUnit's lead in using an annotation (`@Property`) 
to mark plain functions as executable property...

## Setting Up Jqwik

### Gradle

### Maven

## Jqwik's Kotlin Support

## Alternatives to jqwik
