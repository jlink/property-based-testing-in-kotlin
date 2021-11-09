package pbt.kotlin.jqwik.cards

data class PlayingCard(val suit: Suit, val rank: Rank) : Comparable<PlayingCard> {
    override fun toString(): String {
        return String.format("%s of %s", rank, suit)
    }

    override fun compareTo(other: PlayingCard): Int {
        val suitCompare = suit.compareTo(other.suit)
        return if (suitCompare == 0) {
            rank.compareTo(other.rank)
        } else {
            suitCompare
        }
    }
}