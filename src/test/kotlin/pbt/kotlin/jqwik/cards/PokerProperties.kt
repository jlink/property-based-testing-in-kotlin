package pbt.kotlin.jqwik.cards

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.domains.Domain
import net.jqwik.api.statistics.Statistics
import org.assertj.core.api.Assertions.assertThat

@Domain(PokerDomain::class)
class PokerProperties {

    @Property
    fun all52PossibleCardsAreGenerated(@ForAll card: PlayingCard) {
        println(card)
    }

    @Property
    fun shuffledDecksAreGenerated(@ForAll deck: List<PlayingCard>) {
        // System.out.println(deck)
        Statistics.collect(deck[0]) // Collect statistics for first card in deck
        assertThat(deck).hasSize(52)
        assertThat(HashSet(deck)).hasSize(52)
    }

    @Property
    fun aHandHas5UniqueCards(@ForAll hand: Hand) {
        // System.out.println(hand)
        assertThat(hand.show()).hasSize(5)
        assertThat(HashSet(hand.show())).hasSize(5)
    }

    @Property
    fun twoHandsDontShareCards(@ForAll twoHands: Pair<Hand, Hand>) {
        val first = twoHands.first
        val second = twoHands.second
        assertThat(first.show()).hasSize(5)
        assertThat(second.show()).hasSize(5)
        assertThat(first.show()).doesNotContainAnyElementsOf(second.show())
    }

}