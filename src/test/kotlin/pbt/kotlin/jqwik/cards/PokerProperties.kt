package pbt.kotlin.jqwik.cards

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.domains.Domain
import net.jqwik.api.domains.DomainContext
import net.jqwik.api.domains.DomainList
import net.jqwik.api.lifecycle.PerProperty
import net.jqwik.api.lifecycle.PropertyExecutionResult
import net.jqwik.api.statistics.Statistics
import org.assertj.core.api.Assertions.assertThat

@DomainList(
    Domain(PokerDomain::class),
    Domain(DomainContext.Global::class)
)
class PokerProperties {

    @Property
    @PerProperty(All52Cards::class)
    fun `all 52 possible cards are generated`(@ForAll card: PlayingCard) {
        println(card)
    }

    class All52Cards : PerProperty.Lifecycle {
        override fun after(result: PropertyExecutionResult) {
            assertThat(result.countTries()).isEqualTo(52)
        }
    }

    @Property
    fun `shuffled decks are generated`(@ForAll deck: List<PlayingCard>) {
        // System.out.println(deck)
        Statistics.collect(deck[0]) // Collect statistics for first card in deck
        assertThat(deck).hasSize(52)
        assertThat(HashSet(deck)).hasSize(52)
    }

    @Property
    fun `a hand has 5 unique cards`(@ForAll hand: Hand) {
        // System.out.println(hand)
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

