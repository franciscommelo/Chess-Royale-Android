package pt.isel.pdm.chess4android.model.moves

import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.Board


fun Board.isOnCheck(color: Army) = whoIsCheckingKingColored(color).isNotEmpty()

fun Board.whoIsCheckingKingColored(color: Army): List<Int> {
    val kingPosition = if (color == Army.BLACK) getWhiteKingPosition() else getBlackKingPosition()
    return squaresWithPiecesColored(color).filter {
        canPieceMoveFrom(it, kingPosition!!)
    }
}