package pbt.kotlin.jqwik.players

import net.jqwik.api.*
import net.jqwik.api.Arbitraries.forType
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.ofLength
import net.jqwik.kotlin.api.combine

@PropertyDefaults(tries = 10)
class PlayerGeneratorsExamples {

    @Property
    fun playersFromType(@ForAll("fromType") player: Player) {
        println(player)
    }

    @Provide
    fun fromType() : Arbitrary<Player> = forType(Player::class.java)

    @Property
    fun validPlayers(@ForAll("players") player: Player) {
        println(player)
    }

    @Provide
    fun players() = combine(nicknames(), rankings(), roles()) {ni, ra, ro -> Player(ni, ra, ro)}

    fun nicknames() : Arbitrary<String> = String.any().alpha().numeric().ofLength(1..12)

    fun rankings() : Arbitrary<Int> = Int.any(0..1000)

    fun roles() : Arbitrary<String> = Arbitraries.of("dealer", "blind", "standard")

}