package pbt.kotlin.jqwik.cards

data class Hand(val cards: List<PlayingCard>) {
    fun show(): List<PlayingCard> {
        return cards.sorted()
    }
}