package pt.isel.pdm.chess4android.model.moves

import pt.isel.pdm.chess4android.model.*
import kotlin.math.abs

fun Board.getKingMovesFrom(source: Square): List<Move> {

    if (getSquareOccupantAsChessPiece(source)?.second != Piece.KING)
        throw IllegalStateException("Type must be KING!")

    return this.squaresMap.keys.filter { destination ->
        canKingMoveFrom(source, destination) &&
                destinationIsEmptyOrHasEnemy(destination,
                    getSquareOccupantAsChessPiece(source)?.first)
    }.map { destination ->
        val value = if (abs(column(source) - column(destination)) == 2)
            Castling(source, destination)
        else
            RegularMove(source, destination)
        value
    }
}

fun Board.canKingMoveFrom(source: Square, destination: Square): Boolean {

    return destination != source &&
            (abs(row(destination) - row(source)) <= 1 && abs(column(destination) - column(source)) <= 1)

            ||

            run {

                val kingColumn = column(source)
                val destColumn = column(destination)

                (areSquaresClearOnSameRow(source,
                    destination) && abs(destColumn - kingColumn) == 2 &&
                        squaresMap[getCastlePartnerSourceForKingMove(source,
                            destination)].let { it != null && it.second == Piece.ROOK } &&
                        areSquaresClearOnSameRow(source,
                            getCastlePartnerSourceForKingMove(source, destination)))
            }

}

fun Board.getCastlePartnerSourceForKingMove(source: Square, destination: Square): Square {
    return square(row(source), if (column(source) > column(destination)) 0 else columns - 1)
}

fun Board.getCastlePartnerDestinationForKingMove(source: Square, destination: Square): Square {
    return if (column(source) > column(destination)) source - 1 else source + 1
}