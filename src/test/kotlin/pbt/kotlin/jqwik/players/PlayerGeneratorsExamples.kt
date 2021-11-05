package pbt.kotlin.jqwik.players

import net.jqwik.api.*
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.anyForType
import net.jqwik.kotlin.api.ofLength
import net.jqwik.kotlin.api.combine

@PropertyDefaults(tries = 10)
class PlayerGeneratorsExamples {

    @Property
    fun playersFromType(@ForAll("playersByType") player: Player) {
        println(player)
    }

    @Provide
    fun playersByType() : Arbitrary<Player> = anyForType<Player>()

    @Property
    fun validPlayers(@ForAll("players") player: Player) {
        println(player)
    }

    @Provide
    fun players() = combine(nicknames(), rankings(), positions(), ::Player)
    //    fun players() = combine(nicknames(), rankings(), positions()) { n, r, p -> Player(n, r, p)}

    fun nicknames() : Arbitrary<String> = String.any().alpha().numeric().ofLength(1..12)

    fun rankings() : Arbitrary<Int> = Int.any(0..1000)

    fun positions() : Arbitrary<String> = Arbitraries.of("dealer", "forehand", "middlehand")

}