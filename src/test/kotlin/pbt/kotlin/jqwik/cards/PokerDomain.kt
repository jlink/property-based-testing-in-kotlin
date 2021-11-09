package pbt.kotlin.jqwik.cards

import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.Provide
import net.jqwik.api.domains.DomainContextBase
import net.jqwik.kotlin.api.combine

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