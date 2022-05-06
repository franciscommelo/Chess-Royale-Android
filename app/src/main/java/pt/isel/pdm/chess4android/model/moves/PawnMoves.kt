package pt.isel.pdm.chess4android.model.moves

import pt.isel.pdm.chess4android.model.*
import kotlin.math.abs


fun Board.getPawnMovesFrom(source: Square, FEN: Boolean): List<Move> {

    try {
        if (getSquareOccupantAsChessPiece(source)?.second != Piece.PAWN)
            throw IllegalStateException("Type must be PAWN!")

        return this.squaresMap.keys.filter { destination ->
            canPawnMoveFrom(source, destination, FEN) &&
                    destinationIsEmptyOrHasEnemy(
                        destination,
                        getSquareOccupantAsChessPiece(source)?.first
                    )
        }.map { destination ->
            if (column(destination) != column(source) && squareEmpty(destination))
                EnPassant(source, destination)
            else if (row(destination) == 0)
                Promotion(source, destination, promotionType = Piece.QUEEN)
            else
                RegularMove(source, destination)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return emptyList()
    }
}

fun Board.canPawnMoveFrom(source: Square, destination: Square, FEN: Boolean): Boolean {

    val pawn = squaresMap[source]
    val army = pawn?.first
    val player: Army
    val opponent: Army



    if (army == Army.BLACK) {
        player = Army.BLACK
        opponent = Army.WHITE
    } else {
        opponent = Army.BLACK
        player = Army.WHITE
    }

    if (!FEN) {
        if (!(((pawn?.first == opponent) && (row(destination) > row(source))) ||
                    ((pawn?.first == player) && (row(destination) < row(source))))
        )
            return false
    } else {
        if (!(((pawn?.first == Army.BLACK) && (row(destination) > row(source))) ||
                    ((pawn?.first == Army.WHITE) && (row(destination) < row(source))))
        )
            return false
    }



    return (abs(row(destination) - row(source)) == 2 && areSquaresClearOnSameColumn(source,
        destination) && squareEmpty(destination)) && (row(source) == 6 || row(source) == 1)
            ||

            // The pawn wants to step forward one step
            (abs(row(destination) - row(source)) == 1 && areSquaresOnSameColumn(source,
                destination) && squareEmpty(destination))

            ||

            (abs(row(destination) - row(source)) == 1 && abs(column(destination) - column(source)) == 1 && destinationContainsAnEnemy(
                destination,
                pawn.first))

            ||

            (abs(row(destination) - row(source)) == 1 && abs(column(destination) - column(source)) == 1 && squareEmpty(
                destination) &&

                    run {
                        val enPassantVictimPosition = square(row(source), column(destination))
                        squaresMap[enPassantVictimPosition].let {
                            if (it == null)
                                return false

                            if (it.second != Piece.PAWN)
                                return false

                            if (it.first == pawn.first)
                                return false
                        }
                        return true
                    })
    return false
}