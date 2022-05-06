package pt.isel.pdm.chess4android.model.moves

import pt.isel.pdm.chess4android.model.Board
import pt.isel.pdm.chess4android.model.Move
import pt.isel.pdm.chess4android.model.Piece
import pt.isel.pdm.chess4android.model.Square


fun Board.canPieceMoveFrom(source: Int, destination: Int): Boolean {

    if (squaresMap[source] == null || squaresMap[source]?.first == squaresMap[destination]?.first)
        return false

    return when ((squaresMap[source]?.second)) {
        Piece.QUEEN -> canQueenMoveFrom(source, destination)
        Piece.KING -> canKingMoveFrom(source, destination)
        Piece.KNIGHT -> canKnightMoveFrom(source, destination)
        Piece.BISHOP -> canBishopMoveFrom(source, destination)
        Piece.ROOK -> canRookMoveFrom(source, destination)
        Piece.PAWN -> canPawnMoveFrom(source, destination, false)
        else ->
            TODO()
    }
}

fun Board.getPossibleMovesFrom(source: Square, FEN: Boolean): List<Move> {

    if (squareEmpty(source))
        return emptyList()


    val possibleMoves = when (getSquareOccupantAsChessPiece(source)?.second) {
        Piece.QUEEN -> {
            getQueenMovesFrom(source)
        }
        Piece.KING -> {
            getKingMovesFrom(source)
        }
        Piece.KNIGHT -> {
            getKnightMovesFrom(source)
        }
        Piece.BISHOP -> {
            getBishopMovesFrom(source)
        }
        Piece.ROOK -> {
            getRookMovesFrom(source)
        }
        Piece.PAWN -> {
            getPawnMovesFrom(source, FEN)
        }
        Piece.MOVE -> TODO()
        null -> TODO()
    }

    return if (!FEN) {
        possibleMoves.filter {
            it.destination !in listOf(getWhiteKingPosition(), getBlackKingPosition())
        }
    } else
        possibleMoves
}


