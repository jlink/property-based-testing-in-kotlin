package pbt.kotlin.jqwik.players

import net.jqwik.api.*
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.use
import net.jqwik.kotlin.api.ofLength

@PropertyDefaults(tries = 10)
class BuildersExamples {

    @Property
    fun validPlayers(@ForAll("players") player: Player) {
        println(player)
    }

    @Provide
    fun players() : Arbitrary<Player> {
        val builder = Builders.withBuilder { PlayerBuilder() }
        return builder
            .use(nicknames()) {b, n -> b.withNickname(n)}
            .use(rankings()) {b, r -> b.withRanking(r)}
            .use(positions()) {b, p -> b.withPosition(p)}
            .build { it.build()}
    }

    fun nicknames() : Arbitrary<String> = String.any().alpha().numeric().ofLength(1..12)

    fun rankings() : Arbitrary<Int> = Int.any(0..999)

    fun positions() : Arbitrary<String> = Arbitraries.of("dealer", "forehand", "middlehand")

}