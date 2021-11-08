package pbt.kotlin.jqwik.players

class PlayerBuilder() {
    var nickname: String = "joe"
    var ranking: Int = 1
    var position: String = "middlehand"

    fun withNickname(nickname: String): PlayerBuilder {
        this.nickname = nickname
        return this
    }

    fun withRanking(ranking: Int): PlayerBuilder {
        this.ranking = ranking
        return this
    }

    fun withPosition(position: String): PlayerBuilder {
        this.position = position
        return this
    }

    fun build() : Player = Player(nickname, ranking, position)
}