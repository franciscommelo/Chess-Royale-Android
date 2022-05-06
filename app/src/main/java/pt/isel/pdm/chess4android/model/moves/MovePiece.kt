package pt.isel.pdm.chess4android.model.moves

import pt.isel.pdm.chess4android.model.*

fun Board.move(move: Move): Boolean {

    val movingPiece = try {
        getSquareOccupantAsChessPiece(move.source)
    } catch (e: Exception) {
        return false
    }

    when (move) {
        is RegularMove -> {
            getSquareOccupantAsChessPieceOrNull(move.destination)?.let { captive ->
                move.isCapture = true
            }
            setSquareOccupant(move.source, null)
            setSquareOccupant(move.destination, movingPiece)
        }
        is Promotion -> {
            val promotionPiece = movingPiece?.let {
                Pair(
                    it.first, Piece.QUEEN,
                )
            }

            getSquareOccupantAsChessPieceOrNull(move.destination)?.let {
                move.isCapture = true
            }


            setSquareOccupant(move.source, null)
            setSquareOccupant(move.destination, promotionPiece)
        }
        is EnPassant -> {
            val captivePosition = square(row(move.source), column(move.destination))
            move.isCapture = true
            setSquareOccupant(move.source, null)
            setSquareOccupant(move.destination, movingPiece)
            setSquareOccupant(captivePosition, null)
        }
        is Castling -> {

            val rookSource = getCastlePartnerSourceForKingMove(move.source, move.destination)
            val rookDestination =
                getCastlePartnerDestinationForKingMove(move.source, move.destination)

            val rook = getSquareOccupantAsChessPiece(rookSource)

            setSquareOccupant(move.source, null)
            setSquareOccupant(move.destination, movingPiece)

            setSquareOccupant(rookSource, null)
            setSquareOccupant(rookDestination, rook)
        }
    }

    if (movingPiece?.second == Piece.KING) {
        when (movingPiece.first) {
            Army.BLACK -> blackKingPosition = move.destination
            Army.WHITE -> whiteKingPosition = move.destination
        }
    }
    changeTurn()
    return true
}


fun Board.moveFromCoordinates(coo: String) {
    val source = coordinateToSquare(coo.substring(0, 2))
    val destination = coordinateToSquare(coo.substring(2, 4))
    val sourcePiece = getSquareOccupantAsChessPiece(source)
    lastMove = Pair(source, destination)
    setSquareOccupant(destination, sourcePiece)
    setSquareOccupant(source, null)
    changeTurn()
}


fun Board.undoMove(move: Move): Move? {

    val movingPiece = try {
        getSquareOccupantAsChessPiece(move.destination)
    } catch (e: Exception) {
        return null
    }

    when (move) {
        is RegularMove -> {
            setSquareOccupant(move.source, movingPiece)
            setSquareOccupant(move.destination, null)
        }

        is EnPassant -> {
            val captivePosition = square(row(move.source), column(move.destination))
            setSquareOccupant(move.source, movingPiece)
            setSquareOccupant(move.destination, null)
        }
        is Castling -> {
            val rookSource = getCastlePartnerSourceForKingMove(move.source, move.destination)
            val rookDestination =
                getCastlePartnerDestinationForKingMove(move.source, move.destination)

            val rook = getSquareOccupantAsChessPiece(rookDestination)



            setSquareOccupant(move.source, movingPiece)
            setSquareOccupant(move.destination, null)

            setSquareOccupant(rookSource, rook)
            setSquareOccupant(rookDestination, null)
        }
    }

    if (movingPiece?.second == Piece.KING) {
        when (movingPiece.first) {
            Army.BLACK -> blackKingPosition = move.source
            Army.WHITE -> whiteKingPosition = move.source
        }
    }

    return move
}





