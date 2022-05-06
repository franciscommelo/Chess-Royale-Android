package pt.isel.pdm.chess4android.challenges.model

enum class Player {
    WHITE, BLACK;

    companion object {
        val firstToMove: Player = WHITE
    }

    val other: Player
        get() = if (this == WHITE) BLACK else WHITE
}

