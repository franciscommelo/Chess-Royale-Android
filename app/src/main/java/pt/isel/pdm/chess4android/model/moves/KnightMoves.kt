package pt.isel.pdm.chess4android.model.moves

import pt.isel.pdm.chess4android.model.*
import kotlin.math.abs

fun Board.getKnightMovesFrom(source: Square): List<Move> {

    if (getSquareOccupantAsChessPiece(source)?.second != Piece.KNIGHT)
        throw IllegalStateException("Type must be KNIGHT!")

    return this.squaresMap.keys.filter { destination ->
        canKnightMoveFrom(source, destination) &&
                destinationIsEmptyOrHasEnemy(destination,
                    getSquareOccupantAsChessPiece(source)?.first)
    }.map { destination ->
        RegularMove(source, destination)
    }
}

fun Board.canKnightMoveFrom(source: Square, destination: Square): Boolean {

    return (abs(row(destination) - row(source)) == 2 && abs(column(destination) - column(source)) == 1 ||
            abs(row(destination) - row(source)) == 1 && abs(column(destination) - column(source)) == 2)
}
