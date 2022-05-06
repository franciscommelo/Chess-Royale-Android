package pt.isel.pdm.chess4android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class GameState {
    WAITING, PIECE_TOUCHED, PLAYING_PUZZLE, SETUP_MAP, PUZZLE_FINISHED, SHOW_SOLUTION, RESET_SOLUTION, GAME_FINISHED
}

@Parcelize
data class BlockState(
    val board: Board = Board(8, 8),
    val puzzle : String?,
    val solutionMove : MutableList<String>?,
    val solutionOpponent : MutableList<String>?,
    val gameState : GameState,
    val turn : Army,
    val lastMove : Pair<Square?, Square?>?,
    val currPieceTouchedMoves : MutableList<Square>?
): Parcelable