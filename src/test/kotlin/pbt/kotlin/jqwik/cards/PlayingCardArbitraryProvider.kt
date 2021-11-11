package pbt.kotlin.jqwik.cards

import net.jqwik.api.Arbitrary
import net.jqwik.api.providers.ArbitraryProvider
import net.jqwik.api.providers.TypeUsage
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.combine
import net.jqwik.kotlin.api.isAssignableFrom

class PlayingCardArbitraryProvider : ArbitraryProvider {
    override fun canProvideFor(targetType: TypeUsage) = targetType.isAssignableFrom(PlayingCard::class)

    override fun provideFor(
        targetType: TypeUsage,
        subtypeProvider: ArbitraryProvider.SubtypeProvider
    ): Set<Arbitrary<out Any>> {
        val suit = Enum.any<Suit>()
        val rank = Enum.any<Rank>()
        return setOf(combine(suit, rank, ::PlayingCard).withoutEdgeCases())
    }
}