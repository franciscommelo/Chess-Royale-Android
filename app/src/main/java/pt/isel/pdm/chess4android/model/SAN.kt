package pt.isel.pdm.chess4android.model

import pt.isel.pdm.chess4android.model.moves.column
import pt.isel.pdm.chess4android.model.moves.getPossibleMovesFrom
import pt.isel.pdm.chess4android.model.moves.row
import pt.isel.pdm.chess4android.model.moves.squaresWithPiecesColoredAndType


fun Board.sanToMove(san: String): Move {

    val _san = san.takeWhile { it !in listOf('+', '#') }


    if (_san == "O-O") {
        if (turn == Army.WHITE)
            return Castling(whiteKingPosition, whiteKingPosition + 2)
                .also { it.san = san }
        else
            return Castling(blackKingPosition, blackKingPosition + 2)
                .also { it.san = san }
    } else if (_san == "O-O-O") {
        if (turn == Army.WHITE)
            return Castling(whiteKingPosition, whiteKingPosition - 2)
                .also { it.san = san }
        else
            return Castling(blackKingPosition, blackKingPosition - 2)
                .also { it.san = san }
    }


    val isCapture = _san.contains("x")
    val isPromotion = _san.contains("=")

    val moveCode = if (isPromotion) _san.split("=")[0] else _san

    val destinationLabel = moveCode.takeLast(2)
    val destination = coordinateToSquare(destinationLabel)
    val sourceSan = if (isCapture)
        moveCode.split("x")[0]
    else
        moveCode.removeSuffix(destinationLabel)

    val movingPieceType =
        try {
            chessPieceTypeWithSanSymbol(sourceSan.take(1))
        } catch (e: IllegalStateException) {
            Piece.PAWN
        }

    val ambiguityResolver = sourceSan.removePrefix(pieceToSymbol(movingPieceType))

    val source = when (ambiguityResolver.length) {
        0 -> {
            getSquareOfPieceOfTypeThatCanMoveTo(movingPieceType, destination)
        }
        1 -> {
            if (ambiguityResolver.toIntOrNull() == null) {
                val column = fileToColumn(ambiguityResolver.lowercase())
                getSquareOfPieceOfTypeThatCanMoveTo(movingPieceType, destination, column = column)
            } else {
                val row = rankToRow(ambiguityResolver)
                getSquareOfPieceOfTypeThatCanMoveTo(movingPieceType, destination, row = row)
            }
        }
        2 -> {
            coordinateToSquare(ambiguityResolver)
        }
        else -> throw IllegalStateException("Unexpected san format!")
    }

    if (isPromotion) {
        val promotionPieceType = chessPieceTypeWithSanSymbol(_san.split("=")[1])
        return Promotion(source, destination, promotionPieceType)
            .also {
                it.isCapture = isCapture
                it.san = san
            }
    } else if (isCapture && movingPieceType == Piece.PAWN && squareEmpty(destination)) {
        return EnPassant(source, destination)
            .also {
                it.isCapture = true
                it.san = san
            }
    } else {
        return RegularMove(source, destination)
            .also {
                it.isCapture = isCapture
                it.san = san
            }
    }
}


fun Board.coordinateToSquare(coordinate: String): Square {
    return square(rankToRow(coordinate.takeLast(1)), fileToColumn(coordinate.take(1)))
}


fun Board.rankToRow(rank: String): Int {
    return rows - (rank.toInt())
}

fun fileToColumn(file: String): Int {
    return when (file) {
        "a" -> 0
        "b" -> 1
        "c" -> 2
        "d" -> 3
        "e" -> 4
        "f" -> 5
        "g" -> 6
        "h" -> 7
        else -> throw IllegalStateException("Standard square name requires an 8x8 board!")
    }
}

fun columnToFile(column: Int): String {
    return when (column) {
        0 -> "a"
        1 -> "b"
        2 -> "c"
        3 -> "d"
        4 -> "e"
        5 -> "f"
        6 -> "g"
        7 -> "h"
        else -> throw IllegalStateException("Standard square name requires an 8x8 board!")
    }
}

fun Board.getSquareOfPieceOfTypeThatCanMoveTo(
    pieceType: Piece,
    destination: Square,
    column: Int? = null,
    row: Int? = null,
): Square {
    return squaresWithPiecesColoredAndType(turn, pieceType).filter { square ->
        (column?.let { it == column(square) } ?: true) &&
                (row?.let { it == row(square) } ?: true)
    }.first { square ->
        destination in getPossibleMovesFrom(square, true).map { it.destination }
    }
}

fun reverseCoordinates(coo: String): String {
    var reversedCoo: String = ""
    for (c in coo.toCharArray()) {
        reversedCoo +=
            if (c.isDigit()) {
                when (c) {
                    '1' -> "8"
                    '2' -> "7"
                    '3' -> "6"
                    '4' -> "5"
                    '5' -> "4"
                    '6' -> "3"
                    '7' -> "2"
                    '8' -> "1"
                    else -> throw IllegalStateException("Invalid String")
                }
            } else {
                when (c) {
                    'a' -> "h"
                    'b' -> "g"
                    'c' -> "f"
                    'd' -> "e"
                    'e' -> "d"
                    'f' -> "c"
                    'g' -> "b"
                    'h' -> "a"
                    else -> throw IllegalStateException("Invalid String")
                }

            }

    }
    return reversedCoo

}

