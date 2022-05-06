package pt.isel.pdm.chess4android.model.moves

import pt.isel.pdm.chess4android.model.*


fun Board.getBishopMovesFrom(source: Square): List<Move> {

    if (getSquareOccupantAsChessPiece(source)?.second != Piece.BISHOP)
        throw IllegalStateException("Type must be BISHOP!")

    return this.squaresMap.keys.filter { destination ->
        canBishopMoveFrom(source, destination) &&
                destinationIsEmptyOrHasEnemy(
                    destination,
                    getSquareOccupantAsChessPiece(source)?.first
                )
    }.map { destination ->
        RegularMove(source, destination)
    }
}

fun Board.canBishopMoveFrom(source: Square, destination: Square): Boolean {
    return destination != source && areSquaresClearOnSameDiagonal(destination, source)
}