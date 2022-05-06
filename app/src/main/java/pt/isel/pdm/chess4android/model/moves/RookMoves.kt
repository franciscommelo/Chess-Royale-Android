package pt.isel.pdm.chess4android.model.moves


import pt.isel.pdm.chess4android.model.Piece
import pt.isel.pdm.chess4android.model.Square
import pt.isel.pdm.chess4android.model.Board
import pt.isel.pdm.chess4android.model.Move
import pt.isel.pdm.chess4android.model.RegularMove


fun Board.getRookMovesFrom(source: Square): List<Move> {

    if (getSquareOccupantAsChessPiece(source)?.second != Piece.ROOK)
        throw IllegalStateException("Type must be ROOK!")

    return this.squaresMap.keys.filter { destination ->
        canRookMoveFrom(source, destination) &&
                destinationIsEmptyOrHasEnemy(destination,
                    getSquareOccupantAsChessPiece(source)?.first)
    }.map { destination ->
        RegularMove(source, destination)
    }
}

fun Board.canRookMoveFrom(source: Square, destination: Square): Boolean {

    return destination != source &&
            (areSquaresClearOnSameRow(destination, source) || areSquaresClearOnSameColumn(
                destination,
                source))
}