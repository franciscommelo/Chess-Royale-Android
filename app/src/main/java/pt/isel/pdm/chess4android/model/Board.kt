package pt.isel.pdm.chess4android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*


@Parcelize
data class Board(
    var rows: Int = 8,
    var columns: Int = 8,
    var squaresMap: HashMap<Int, Pair<Army, Piece>?> = hashMapOf(),
    var turn: Army = Army.WHITE,
    var blackKingPosition: Int = 0,
    var whiteKingPosition: Int = 0,
    var currPieceTouches: Square? = null,
    var lastMove: Pair<Square?, Square?>? = null,

    ) : Parcelable {

    fun changeTurn() {
        turn = when (turn) {
            Army.BLACK -> Army.WHITE
            Army.WHITE -> Army.BLACK
        }
    }

    fun reset() {
        repeat(rows * columns) { index ->
            squaresMap[index] = null
        }
    }

    companion object {
        // standard starting chess board - FEN
        const val STARTING_POSITION_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"
    }

    fun loadStandardStartingPosition() {
        if (columns != 8 || rows != 8)
            throw IllegalStateException("This method requires an 8x8 board!")
        loadPositionFromFen(STARTING_POSITION_FEN)
    }


    // Load board from a FEN notation string
    // Learn more -> https://www.chess.com/terms/fen-chess
    fun loadPositionFromFen(fen: String) {
        reset()
        blackKingPosition = 4
        whiteKingPosition = 60
        val pieceArrangement = fen.takeWhile { it != ' ' }
        val rowPositions = pieceArrangement.split("/")
        var column: Int
        for ((row, positions) in rowPositions.withIndex()) {
            column = 0
            for (element in positions) {
                val square = row * rows + column
                val fenSymbol = element.toString()
                if (fenSymbol.toIntOrNull() != null) {
                    setSquareOccupant(square, null)
                    column += fenSymbol.toInt()
                } else {
                    val piece = Pair(
                        if (element.isUpperCase()) Army.WHITE else Army.BLACK,
                        chessPieceTypeWithFenSymbol(fenSymbol)
                    )
                    setSquareOccupant(square, piece)
                    column++
                }
            }
        }
    }

    fun boardToFEN(): String? {
        var fen: String? = ""

        for (num in 7 downTo 0) {
            var emptyCounter = 0

            for (collumn in 7 downTo 0) {

              val curr = squaresMap[square(num, collumn)]


                if (curr?.second != null) {
                    if (emptyCounter != 0) {
                        fen += emptyCounter
                        emptyCounter = 0
                    }
                    val pieceColor: Army = curr.first
                    val name: String = pieceToSymbol(curr.second)
                    if (pieceColor == Army.WHITE) {
                        fen += name
                    } else {
                        fen += name.lowercase(Locale.getDefault())
                    }
                } else {
                    emptyCounter++
                }
            }
            if (emptyCounter != 0) {
                fen += emptyCounter
            }
            if (num != 0) {
                fen += "/"
            }
        }
        return fen
    }



    fun setSquareOccupant(square: Square, piece: Pair<Army, Piece>?) {
        squaresMap[square] = piece
    }

    fun getSquareOccupantAsChessPiece(square: Square): Pair<Army, Piece>? {
        return squaresMap[square]
    }

    fun square(row: Int, column: Int): Square = row * rows + column

    fun squareEmpty(square: Square) = squaresMap[square] == null


    fun destinationIsEmptyOrHasEnemy(destination: Square, color: Army?): Boolean {
        return if (squareEmpty(destination))
            true
        else
            getSquareOccupantAsChessPiece(destination)?.first != color
    }

    fun destinationContainsAnEnemy(destination: Square, color: Army): Boolean {
        squaresMap[destination].let {
            return it != null && it.first != color
        }
    }

    fun getSquareOccupantAsChessPieceOrNull(square: Square): Pair<Army, Piece?>? {
        return if (squareEmpty(square)) null else squaresMap[square]
    }

    fun opponent(army: Army): Army {
        return if (army == Army.WHITE) Army.BLACK else Army.WHITE
    }


    fun getBlackKingPosition() : Square? = squaresMap.filterValues { it == Pair(Army.BLACK, Piece.KING)}.keys.first()

    fun getWhiteKingPosition() : Square? = squaresMap.filterValues { it == Pair(Army.WHITE, Piece.KING)}.keys.first()

}
