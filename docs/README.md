# Property-based Testing in Kotlin

Kotlin is currently the most hyped language on the JVM. With good reason.
Kotlin is mostly compatible with Java and comes with a nice set of features 
to make programming on the JVM less burdensome and more functional.

However, there is not much coverage of _Property-based Testing_ (PBT) focusing on Kotlin.
Some of the few articles and resources I found are

- [A short blog series](https://medium.com/default-to-open/property-based-testing-in-kotlin-part-1-56929927b8b8)
- [A chapter of "The Joy of Kotlin"](https://livebook.manning.com/book/the-joy-of-kotlin/b-property-based-testing-in-kotlin/v-8/156)

To fill the gap a bit this article covers the application of PBT in Kotlin 
using [jqwik's](https://jqwik.net) [Kotlin module](https://jqwik.net/docs/snapshot/user-guide.html#kotlin-module).

<!-- Generated toc must be stripped of `nbsp` occurrences in links -->
<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
## Table of Contents  

- [A Short Intro to PBT](#a-short-intro-to-pbt)
- [Jqwik, JUnit Platform and Kotlin](#jqwik-junit-platform-and-kotlin)
- [Setting Up Jqwik](#setting-up-jqwik)
- [Success, Failure and Shrinking](#success-failure-and-shrinking)
- [Generators (aka Arbitraries)](#generators-aka-arbitraries)
  - [Programming Generators](#programming-generators)
    - [Generate by Type](#generate-by-type)
    - [Choosing Constrained Base Arbitraries](#choosing-constrained-base-arbitraries)
  - [Configuring, Transforming and Combining Arbitraries](#configuring-transforming-and-combining-arbitraries)
    - [Configuration through Annotation](#configuration-through-annotation)
    - [Configuration through Fluent API](#configuration-through-fluent-api)
  - [Transforming Individual Arbitraries](#transforming-individual-arbitraries)
    - [Filtering](#filtering)
    - [Mapping](#mapping)
  - [Creating Collections and other Multi-Value Types](#creating-collections-and-other-multi-value-types)
  - [Flat Mapping](#flat-mapping)
  - [Assumptions](#assumptions)
  - [Combining Generators](#combining-generators)
  - [Providing Generators through Domain Contexts](#providing-generators-through-domain-contexts)
- [Special Kotlin Support](#special-kotlin-support)
  - [Compatibility](#compatibility)
  - [Nullability](#nullability)
  - [Convenience Methods](#convenience-methods)
  - [Testing of Asynchronous Code](#testing-of-asynchronous-code)
  - [Constraints and Quirks](#constraints-and-quirks)
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

## Success, Failure and Shrinking

Now that you can run your own properties it's worthwhile to look at jqwik's output and reporting. 
When we run this property from above:

```kotlin
@Property
fun `reversing keeps all elements`(@ForAll list: List<Int>) {
    assertThat(list.reversed()).containsAll(list)
}
```

it runs successfully, thereby producing the following output:

```
ReverseProperties:reversing keeps all elements = 
                              |-------------------jqwik-------------------
tries = 1000                  | # of calls to property
checks = 1000                 | # of not rejected calls
generation = RANDOMIZED       | parameters are randomly generated
after-failure = PREVIOUS_SEED | use the previous seed
when-fixed-seed = ALLOW       | fixing the random seed is allowed
edge-cases#mode = MIXIN       | edge cases are mixed in
edge-cases#total = 10         | # of all combined edge cases
edge-cases#tried = 10         | # of edge cases tried in current run
seed = -8839434152225186972   | random seed to reproduce generated values
```

With default configuration this report will be published for each and every property.
It tells you how often a property function has been started (`tries`), 
how often it has actually been evaluated (`checks`),
the random `seed` that can be used for replicating the exact same generated test data,
and other information of lesser interest.

Let's see what happens if a property fails.
The following property suggests that `list.reversed()` does nothing:

```kotlin
@Property
fun `reversing keeps the list unchanged`(@ForAll list: List<Int>) {
    assertThat(list.reversed()).isEqualTo(list)
}
```

It produces a report similar to this:

```
ReverseProperties:reversing keeps the list unchanged = 
  org.opentest4j.AssertionFailedError:
    expected: [0, 1]
     but was: [1, 0]

                              |-------------------jqwik-------------------
tries = 1                     | # of calls to property
checks = 1                    | # of not rejected calls
generation = RANDOMIZED       | parameters are randomly generated
after-failure = PREVIOUS_SEED | use the previous seed
when-fixed-seed = ALLOW       | fixing the random seed is allowed
edge-cases#mode = MIXIN       | edge cases are mixed in
edge-cases#total = 10         | # of all combined edge cases
edge-cases#tried = 0          | # of edge cases tried in current run
seed = -5786297582797313483   | random seed to reproduce generated values

Shrunk Sample (11 steps)
------------------------
  list: [0, 1]

Original Sample
---------------
  list:
    [
      -5459970, -113449, -15929, 151198, -657, -50279, 47614, 5813, 972, -23847, 1227, -19, -111, 
      -12, -4380, 205, 51669, 20, 984
    ]
```

In addition to the `AssertionFailedError` two sets of data are being shown:
the _original sample_ and the _shrunk sample_.
Both show data that make the property fail. 
Whereas the former is (pseudo-)randomly generated, the latter is the result of _shrinking_ the original set.
_Shrinking_ is PBT lingo for taking the original failing sample, making it somewhat "smaller" 
and re-running the property function with this simplified version, until no smaller sample can be found.

Shrinking has two big advantages:
- It provides you with a sample that has less complexity and that is therefore easier to reason about.
  In the ideal case, you have now the simplest failing sample, with no accidental complexity.
- If it works perfectly - which it often does not - it will always shrink to the exact same sample.
  That means less non-determinism in your tests, which is a major factor for continuous integration.

Shrinking is a major differentiator when it comes to comparing PBT libraries with each other.
Libraries usually follow one of two approaches:

- Users must define shrinking behaviour together with defining a generator.
  This requires additional effort but also allows for domain-specific, targeted shrinking.
- Shrinking behaviour is automatically derived from a generator's specification.
  This is more convenient for users but may result in worse shrinking results in some cases.

Another characteristic to differentiate between shrinking approaches is  
[type-based versus integrated shrinking](https://hypothesis.works/articles/integrated-shrinking/).

_jqwik_ is a strict proponent of shrinking being an intrinsic part of generation - with some tweaking possible.
It also comes with fully integrated shrinking.
For the user of a library that's the all-round carefree package, if it works as expected, which it usually does.


## Generators (aka Arbitraries)

_jqwik_ comes with quite a few built-in generators for many of Java's and Kotlin's fundamental types.
When it comes to your own programs, however, you'll soon have the desire to generate objects of your specific target domain.

### Programming Generators

To build a domain-specific generator the procedure is usually as follows:
- Identify the base types from which to build your domain type.
- Choose and configure the generators for your base types.
- Map, filter and combine the base generators into building instances of your domain type.
- Use your domain type generators for composing more complex domain generators if necessary.

Although they are often called _generators_, in jqwik they are represented by the interface `Arbitrary<T>`,
where `T` is the type of the values to generate.
Think of `Arbitrary` as an abstraction for _factories of generators_.
This abstraction comes with a lot of useful features to transform and combine it; 
functional programmers may even call it _monadic_.

Let's look at how to build an arbitrary for the following type, 
which is supposed to represent participants of a card game:

```kotlin
data class Player(val nickname: String, val ranking: Int, val postion: String)
```

I've chosen to use a data class here to make the code compact, not because it's necessarily the best option.
Using a fully fledged class would not change the approach towards building a generator.

#### Generate by Type

A `Player` has a nickname of type `String`, a ranking of type `Int` and a position that's also a `String`.
When all underlying types are built into jqwik (or made known to jqwik) there's a very simple way 
for generating instances of a given type by using `Arbitraries.forType(Class<T>)`.
Let's try that with `Player`:

```kotlin
@Property
fun playersFromType(@ForAll("playersByType") player: Player) {
    println(player)
}

@Provide
fun playersByType() : Arbitrary<Player> = anyForType<Player>()
```

In this example you can see how you can tell a parameter to use a generator from a _provider function_:
Make a function that returns an `Arbitrary<YourDomainType>`, 
position this function in the same container as your property function,
add annotation `@Provide` to the function
and use the function's name in the parameter's `ForAll` annotation.

Often, the explicit return type can be left out since it will be automatically inferred by the Kotlin compiler. 
Mind that in this exact example either the function's return type or the explicit type parameter
of `anyForType<Player>()` is necessary.

When we run this example, the output will look something like

```
Player(nickname=秨ㅋ綞蛈蒅천綃붯ⷊ䴊츋㗨ঽ䛵粅⟌ఘᘍ슀ΐ읛핢뾴㤅㾣행⢯䫗臼힛⌆, ranking=11322, position=舶紭䜫᤬૚诖녅슎扮葱)
Player(nickname=蛈蒅천綃붯ⷊ䴊츋, ranking=105341, position= )
Player(nickname=천綃붯ⷊ, ranking=2147483647, position= )
Player(nickname=붯ⷊ䴊츋㗨ঽ䛵粅⟌, ranking=-2631, position=㾣행跎)
Player(nickname=䴊츋㗨ঽ, ranking=1733, position=읛핢뾴㤅)
Player(nickname=츋㗨ঽ䛵粅⟌, ranking=-2631, position=㾣행跎)
Player(nickname=㗨ঽ䛵粅⟌ఘᘍ슀, ranking=1439489, position=宄඄)
Player(nickname=䛵粅⟌ఘᘍ슀ΐ읛핢뾴㤅擪떏旀椼¤ꤊ翿咢잖趝ᢉᱜ纁틏Ⴧ萚ۊᬙ휘痩뻩븦䧗輮䯟䀺엮Ȣ﮸Ĉ寶倅撿ӗᶗ, ranking=-3183, position=痩뻩)
Player(nickname=粅⟌ఘᘍ, ranking=-12, position=행)
Player(nickname=⟌, ranking=-2631, position=㾣행跎)
...
```

Without further information, jqwik will generate 
- an _arbitrary_ string as `nickname` and `position` - even empty strings or unprintable Unicode chars are within range
- an _arbitrary_ number of type `Int` - negatives and zero are possible

This might be what you want; 
in many cases, however, meaningful values are more restricted than what the plain Kotlin or Java type can tell us.

#### Choosing Constrained Base Arbitraries

Let's assume the following constraints for a player's attributes:
- Nicknames must have a length of 1 to 12 characters.
- Nicknames can contain English upper and lowercase letters as well as the digits `0` to `9`.
- A player's ranking is between `0` and `999`.
- There are only three possible positions: `dealer`, `forehand` and `middlehand`

This is all we need to know to come up with the three base generators:

```kotlin
fun nicknames() : Arbitrary<String> = String.any().alpha().numeric().ofLength(1..12)
fun rankings() : Arbitrary<Int> = Int.any(0..1000)
fun positions() : Arbitrary<String> = Arbitraries.of("dealer", "forehand", "middlehand")
```

Having those three in place we just combine them into a provider function for players:

```kotlin
@Provide
fun players() = combine(nicknames(), rankings(), positions()) {n, r, p -> Player(n, r, p)}
```

or even more compact, but less readable:

```kotlin
@Provide
fun players() = combine(nicknames(), rankings(), positions(), ::Player)
```

Running now the property

```kotlin
@Property
fun validPlayers(@ForAll("players") player: Player) {
    println(player)
}
```

produces something more to our liking:

```
Player(nickname=a, ranking=2, position=dealer)
Player(nickname=A, ranking=0, position=dealer)
Player(nickname=z, ranking=0, position=dealer)
Player(nickname=RwClZUm7fPQ, ranking=1000, position=forehand)
Player(nickname=7ggjAy1RK, ranking=721, position=forehand)
Player(nickname=z, ranking=0, position=dealer)
Player(nickname=TyfnGRbpqav1, ranking=234, position=dealer)
Player(nickname=TGuRgQ1MO03, ranking=2, position=dealer)
Player(nickname=0, ranking=38, position=dealer)
Player(nickname=5F, ranking=5, position=middlehand)
...
```

### Configuring, Transforming and Combining Arbitraries

With the `combine` function we have already seen one of the fundamental ways to use an arbitrary (or several)
in order to arrive at another one.
There's much more, though.

#### Configuration through Annotation

The simplest way to influence what your generators produce is by using specific annotations,
the purpose of which is to provide information about constraints.
You have already seen a few of those annotations:
- `@Size` can constraining the size of multi-value types like `List`, `Set`, arrays and others.
- `@NotBlank` tells String generators to never generate blank Strings (empty or whitespace only).

There's many of them; and their number is constantly growing.
They are described in the user guide's chapter on [Constraining Default Generation](https://jqwik.net/docs/current/user-guide.html#constraining-default-generation).


#### Configuration through Fluent API

When you provide generators programmatically, though, annotations are of no help.
That's why most built-in arbitrary types come with a fluent configuration API.
Let's take String generation as an example:

```java
public interface StringArbitrary extends Arbitrary<String> {
	StringArbitrary ofMaxLength(int maxLength);
	StringArbitrary ofMinLength(int minLength);
	StringArbitrary ofLength(int length);
	StringArbitrary withChars(char... chars);
	StringArbitrary withChars(CharSequence chars);
	StringArbitrary withCharRange(char from, char to);
	StringArbitrary ascii();
	StringArbitrary alpha();
	StringArbitrary numeric();
	StringArbitrary whitespace();
	StringArbitrary all();
	StringArbitrary excludeChars(char ... charsToExclude);
	StringArbitrary withLengthDistribution(RandomDistribution lengthDistribution);
	Arbitrary<String> repeatChars(double repeatProbability);
}
```

Creating an arbitrary for Strings of length 2 to 42 with just ascii chars looks like:

```kotlin
Arbitraries.strings().ascii().ofMinLength(2).ofMaxLength(42)
```

The Kotlin module often adds extension functions to make using the API smoother.
Thus, we can rewrite the code above as:

```kotlin
String.any().ascii().ofLength(2..42)
```

You'll find configurable arbitraries for Strings, characters, numbers, collections, dates, times and many others.
The Kotlin module adds APIs for `IntRange` and `Sequence`.
To get a feeling for the breadth of available options look at 
[this section](https://jqwik.net/docs/current/user-guide.html#customized-parameter-generation)
in jqwik's user guide.


### Transforming Individual Arbitraries

Having an existing generator of type `Arbitrary<T>` allows you to do many useful things with it.
Here's an extract of interface `Arbitrary`:

```java
public interface Arbitrary<T> {
  Arbitrary<T> filter(Predicate<T> filterPredicate);
  <U> Arbitrary<U> map(Function<T, U> mapper);
  <U> Arbitrary<U> flatMap(Function<T, Arbitrary<U>> mapper);
  Arbitrary<@NullableType T> injectNull(double nullProbability);
  ListArbitrary<T> list();
  SetArbitrary<T> set();
  StreamArbitrary<T> stream();
  IteratorArbitrary<T> iterator();
  <A> ArrayArbitrary<T, A> array(Class<A> arrayClass);
  Arbitrary<Optional<T>> optional();
}
```

#### Filtering

Filtering is about including values that fulfill a predicate.
For generating only even numbers between 2 and 100000:

```kotlin
Int.any(2..100000).filter {it % 2 == 0}
```

#### Mapping

Mapping is about using a generated value to produce another one.
This allows another approach for even numbers between 2 and 100000:

```kotlin
Int.any(2..50000).map {it * 2}
```

You can map to different types. 
We use that here for creating the HEX value of an integer:

```kotlin
Int.any(1..Int.MAX_VALUE).map { it.toString(16)}
```

### Creating Collections and other Multi-Value Types

Lists, sets, arrays etc. are very common when it comes to building domain specific data types.
Therefore, it should be easy to generate those as well; and it is.
Just use `list()`, `set()`, `stream()`, `iterator()` and `array(..)`:

Here's how you build a generator for lists of 10 doubles:

```kotlin
Double.any().list().ofSize(10)
```

Due to the existence of primitive array types in Java, the creation of arrays requires more ceremony:

```kotlin
Double.any().array(Array<Double>::class.java).ofSize(10)
```

In Kotlin, we have a bit more flexibility, so here's the version using reified types,
which saves you two characters:

```kotlin
Double.any().array<Double, Array<Double>>().ofSize(10)
```

You decide which one you prefer.

### Flat Mapping

_Flat Mapping_ is one of the magic words you have to use if you want to belong to the functional crowd.
The idea behind it is straightforward: 
Instead of using a (generated) value to produce another value, which is a plain _map_,
you produce another _arbitrary_ instead.

Why is that useful?
Now and then you want to generate several arbitrary values; 
but the values are not fully independent of each other but there's some condition linking the two values.
Imagine you had to verify that `List<T>.index(t)` returns the correct index for elements that are present in the list.
Here's a property trying to check that property:

```kotlin
@Property
fun `index works for element in list`(@ForAll list: List<Int>, @ForAll index: Int) {
    val element = list[index]
    assertThat(list.indexOf(element)).isEqualTo(index)
}
```

Maybe you've already expected the failure:

```
FlatMappingExamples:index works for element in list = 
  java.lang.IndexOutOfBoundsException:
    Index 0 out of bounds for length 0

Shrunk Sample (2 steps)
-----------------------
  list: []
  index: 0
```

The problem at hand: the generated value for _index_ does not consider the number of elements 
in the generated list; but it should to make the property really a falsifiable one.
Flat mapping comes to our rescue:

```kotlin
@Property
fun `index works for element in list`(@ForAll("listWithValidIndex") listWithIndex: Pair<List<Int>, Int>) {
    val (list, index) = listWithIndex
    val element = list[index]
    assertThat(list.indexOf(element)).isEqualTo(index)
}

@Provide
fun listWithValidIndex() : Arbitrary<Pair<List<Int>, Int>> {
    val lists = Int.any().list().ofMinSize(1)
    return lists.flatMap { list -> Int.any(0 until list.size).map { index -> Pair(list, index)} }
}
```

The magic happens in the line with the `return` statement:
The size of the generated `list` is used to constrain the upper border of the index generator.
The final trick is to return the two values, the `list` and the `index`, together as a `Pair`.

As a surprise to me, the property still fails:

```
FlatMappingExamples:index works for element in list = 
  org.opentest4j.AssertionFailedError:
    expected: 1
     but was: 0
     
Shrunk Sample (86 steps)
------------------------
  listWithIndex: ([0, 0], 1)
```

Our property did not consider duplicate elements in the list.
One way to get rid of that problem is to make sure elements are unique:

```kotlin
@Provide
fun listWithValidIndex() : Arbitrary<Pair<List<Int>, Int>> {
    val lists = Int.any().list().uniqueElements().ofMinSize(1)
    return lists.flatMap { list -> Int.any(0 until list.size).map { index -> Pair(list, index)} }
}
```

Et voilà, everything running fine now.


### Assumptions

In a way, flat mapping over arbitraries is a rather involved way to deal with dependencies across.
A simpler and sometimes more natural way to reach the same goal are assumptions.
An assumption is a filter that can use all parameters of a property function: 

```kotlin
@Property
fun `index works for element in lis`(
      @ForAll @UniqueElements list: List<Int>, 
      @ForAll index: Int
) {
    Assume.that(index >= 0 && index < list.size)
    val element = list[index]
    assertThat(list.indexOf(element)).isEqualTo(index)
}
```

Here, the assumption `Assume.that(index >= 0 && index < list.size)` makes sure 
that only if `index` is in the valid range, the property will proceed to run.
This filtering out can, however, lead to a lot of test cases that are generated but thrown away afterwards.
If too many are thrown away, jqwik will complain and fail:

```
Property [FlatMappingExamples:index works for element in list - using assumption] exhausted after [1000] tries and [930] rejections
```

We can mitigate that problem by constraining the initial set of generated test cases; for example:

```kotlin
@Property
fun `index works for element in list`(
    @ForAll @UniqueElements @Size(max = 50) list: List<Int>,
    @ForAll @JqwikIntRange(max = 49) index: Int
) {
    //...
}
```

This should succeed, but still, the report shows that more than half 
of the generated test cases are being thrown away:

```
FlatMappingExamples:index works for element in list = 
                              |-------------------jqwik-------------------
tries = 1000                  | # of calls to property
checks = 463                  | # of not rejected calls
```


### Combining Generators

We've already seen the most common way to combine two or more arbitraries into a more complicated one.
The pattern, which scales up to 8 arbitraries, looks as follows:

```kotlin
val arbitrary1: Arbitrary<T1> = ...
val arbitrary2: Arbitrary<T2> = ...
val arbitrary3: Arbitrary<T3> = ...

combine(arbitrary1, arbitrary2, arbitrary3) { t1, t2, t3 ->
    // return a value that uses t1 through t3 for its own creation
}
```

If you need `t1..n` to configure another generator `flatCombine(..)` is what you need.

There is another way to do something similar using a _builder_-based approach.
_Builders_ collect information required for construction - often in functions starting `with...` -
and then create the desired result type in a single step, e.g. by calling `build()`.
Using that approach the `Player` example from above could look as follows:

```kotlin
@Provide
fun players() : Arbitrary<Player> {
    val builder = Builders.withBuilder { PlayerBuilder() }
    return builder
        .use(nicknames()) {b, n -> b.withNickname(n)}
        .use(rankings()) {b, r -> b.withRanking(r)}
        .use(positions()) {b, p -> b.withPosition(p)}
        .build { it.build()}
}
```

Using `Builders.withBuilder(..)` makes sense if:
- You already have builder classes for your domain type
- You have more than 8 attributes to combine
- You consider the builder syntax more readable

In the end `combine(..)` and builders are equivalent; 
sometimes one tends to be more concise (read: less code) than the other.


### Providing Generators through Domain Contexts

In most examples I showed so far, generators / arbitraries were handed in through provider functions.
This is convenient, since it allows to have the specification of generated data 
close to the property function itself.
One thing with this approach is difficult, though: 
Sharing generators across container classes is not easily possible.
You _could_ move common generation logic into a helper class and then delegate to it from provider functions,
which _must_ reside in the class of the property function, or in a supertype, 
or in the case of nested inner classes in one of its containing classes.

Enter jqwik's concept of _domains_. 
A "domain" is a collection of arbitrary providers and configurators that belong together.
In concrete programming terms:
- It's a class that has to implement `net.jqwik.api.domains.DomainContext` or, in most cases,
  that extends `net.jqwik.api.domains.DomainContextBase`.
- Such a domain context implementation is considered for property functions,
  if the function or its container class has the annotation `@Domain(MyDomainContext::class)`.

When a domain context class extends `DomainContextBase` the simplest way to provide a generator
is by moving the provider function from your properties class to the context class, 
the `@Provide` annotation included.

#### The Poker Domain

To demonstrate this, I've chosen Poker, the card game, as an example.
It comes with four domain types:
- enum class `Suit`: SPADES, HEARTS, DIAMONDS, CLUBS
- enum class `Rank`: JOKER, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE
- data class `PlayingCard(val suit: Suit, val rank: Rank)`
- data class `Hand(val cards: List<PlayingCard>)`

What we'll need to generate for testing a Poker engine or a Poker AI are:
- Individual playing cards
- A full deck of 52 cards, shuffled
- Valid Hands with no duplicate cards
- Two or more hands out of a deck
- etc.

All these generators can be placed as functions to a domain context class:

```kotlin
class PokerDomain : DomainContextBase() {
  @Provide
  fun cards(): Arbitrary<PlayingCard> {
    val suit = Arbitraries.of(Suit::class.java)
    val rank = Arbitraries.of(Rank::class.java).filter { r: Rank -> r !== Rank.JOKER }
    return combine(suit, rank) { s: Suit, r: Rank -> PlayingCard(s, r) }.withoutEdgeCases()
  }

  @Provide
  fun decks(): Arbitrary<List<PlayingCard>> {
    return cards().list().uniqueElements().ofSize(52)
  }

  @Provide
  fun hands(): Arbitrary<Hand> {
    return decks().map { deck: List<PlayingCard> -> Hand(deck.subList(0, 5)) }
  }

  @Provide
  fun pairOfHands(): Arbitrary<Pair<Hand, Hand>> {
    return decks().map { deck: List<PlayingCard> ->
      val first = Hand(deck.subList(0, 5))
      val second = Hand(deck.subList(5, 10))
      Pair(first, second)
    }
  }
}
```

In addition to the `@Provide` annotation, the return type of the provider function plays an important role:
The variable part inside `Arbitrary<..>` is used to match the type of a property function's for-all-parameter.
With that in place, the property functions can just use the target type of values to be generated:

```kotlin
@Domain(PokerDomain::class)
class PokerProperties {
    @Property
    fun `shuffled decks are generated`(@ForAll deck: List<PlayingCard>) {
        assertThat(deck).hasSize(52)
        assertThat(HashSet(deck)).hasSize(52)
    }

    @Property
    fun `a hand has 5 unique cards`(@ForAll hand: Hand) {
        assertThat(hand.show()).hasSize(5)
        assertThat(HashSet(hand.show())).hasSize(5)
    }

    @Property
    fun `two hands dont share cards`(@ForAll twoHands: Pair<Hand, Hand>) {
        val first = twoHands.first
        val second = twoHands.second
        assertThat(first.show()).hasSize(5)
        assertThat(second.show()).hasSize(5)
        assertThat(first.show()).doesNotContainAnyElementsOf(second.show())
    }
}
```

These three properties just verify that the generators work as expected.
Now you have the tools to start implementing and testing the actual Poker engine!

#### Advanced Usage of Domains

There's one caveat you should be aware of: 
As soon as you use apply a domain to your property or property class, 
all the built-in generators for Strings, numbers etc. are no longer available by default.
To enable them you have to additionally add `@Domain(DomainContext.Global::class)`.
Since Kotlin does not support repeatable annotations (yet),
add more than one annotation looks a bit cumbersome:

```kotlin
@DomainList(
    Domain(PokerDomain::class),
    Domain(DomainContext.Global::class)
)
```

Provider functions are just the simplest way of adding generators to a domain.
A domain can also have full-fledged arbitrary providers and arbitrary configurators.
Look at [Domain and Domain Contexts](https://jqwik.net/docs/current/user-guide.html#domain-and-domain-context)
in jqwik's user-guide for the full functionality available.

## Special Kotlin Support

The kotlin module has two main goals: 
Smoothing off some rough edges when using the jqwik API in Kotlin 
and supporting (some of) the additional features that Kotlin brings to the table.

### Compatibility

_jqwik_ is compatible with Kotlin's syntax and its somewhat different view on Java's type system. 
That means that:

- You can use functions instead of methods; functions returning `false` indicate a failure.
- 
- Kotlin allows function names - and even class names! - with spaces and a few other special characters;
  you have to use backticks for those names, though.
- 
- You can use Kotlin's expression syntax for property functions, 
  which allows for very concise statements:
  
  ```kotlin
  @Property 
  fun `positive is above 0`(@ForAll @Positive anInt: Int) = anInt > 0
  ```

- You can use all of Kotlin's array and collection types (and have them generated for you).
- 
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

- You can use the different Kotlin ways to define types (data classes, sealed classes, type aliases etc.)
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


## Summary

Property-based testing of stateful systems can get a bit more involved than
using PBT for plain functions and systems without relevant state.
Sometimes it's easy to use post-conditions for the various actions that
can be applied. In other cases it's much easier to write an explicit model that
duplicates some aspects of the implementation under tests and can thereby
serve as an easy-to-use oracle for an action's outcome.

### Feedback

Give feedback, ask questions and tell me about your own PBT experiences
on [Twitter](https://twitter.com/johanneslink).


### Sharing, Code and License

This article is published under the following license:
[Attribution-ShareAlike 4.0 International](https://creativecommons.org/licenses/by-sa/4.0/)

You can find 
[all code examples on github](https://github.com/jlink/property-based-testing-in-kotlin/tree/master/src).
