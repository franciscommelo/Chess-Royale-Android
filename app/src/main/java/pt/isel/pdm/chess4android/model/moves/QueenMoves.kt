package pt.isel.pdm.chess4android.model.moves

import pt.isel.pdm.chess4android.model.*


fun Board.getQueenMovesFrom(source: Square): List<Move> {

    if (getSquareOccupantAsChessPiece(source)?.second != Piece.QUEEN)
        throw IllegalStateException("Type must be QUEEN!")

    return this.squaresMap.keys.filter { destination ->
        canQueenMoveFrom(source, destination) &&
                destinationIsEmptyOrHasEnemy(destination,
                    getSquareOccupantAsChessPiece(source)?.first)
    }.map { destination ->
        RegularMove(source, destination)
    }

}

fun Board.canQueenMoveFrom(source: Square, destination: Square): Boolean {

    return destination != source &&
            (areSquaresClearOnSameRow(source, destination) ||
                    areSquaresClearOnSameColumn(source, destination) ||
                    areSquaresClearOnSameDiagonal(source, destination))
}