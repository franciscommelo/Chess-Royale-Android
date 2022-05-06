package pt.isel.pdm.chess4android.model

enum class ChessPieceType(val sanSymbol: String, val fenSymbol: String) {
    QUEEN("Q", "Q"),
    KING("K", "K"),
    KNIGHT("N", "N"),
    BISHOP("B", "B"),
    ROOK("R", "R"),
    PAWN("", "P")
}

fun chessPieceTypeWithFenSymbol(fenSymbol: String): Piece {
    return when (fenSymbol.uppercase()) {
        ChessPieceType.QUEEN.fenSymbol -> Piece.QUEEN
        ChessPieceType.KING.fenSymbol -> Piece.KING
        ChessPieceType.KNIGHT.fenSymbol -> Piece.KNIGHT
        ChessPieceType.BISHOP.fenSymbol -> Piece.BISHOP
        ChessPieceType.ROOK.fenSymbol -> Piece.ROOK
        ChessPieceType.PAWN.fenSymbol -> Piece.PAWN
        else -> throw IllegalStateException("Unknown FEN symbol!")
    }
}

fun chessPieceTypeWithSanSymbol(sanSymbol: String): Piece {
    return when (sanSymbol) {
        ChessPieceType.QUEEN.sanSymbol -> Piece.QUEEN
        ChessPieceType.KING.sanSymbol -> Piece.KING
        ChessPieceType.KNIGHT.sanSymbol -> Piece.KNIGHT
        ChessPieceType.BISHOP.sanSymbol -> Piece.BISHOP
        ChessPieceType.ROOK.sanSymbol -> Piece.ROOK
        ChessPieceType.PAWN.sanSymbol -> Piece.PAWN
        else -> throw IllegalStateException("Unknown SAN symbol!")
    }
}

fun pieceToSymbol(piece: Piece) : String{
    return when (piece) {
        Piece.QUEEN -> ChessPieceType.QUEEN.sanSymbol
        Piece.KING -> ChessPieceType.KING.sanSymbol
        Piece.KNIGHT -> ChessPieceType.KNIGHT.sanSymbol
        Piece.BISHOP -> ChessPieceType.BISHOP.sanSymbol
        Piece.ROOK ->ChessPieceType.ROOK.sanSymbol
        Piece.PAWN ->  ChessPieceType.PAWN.fenSymbol
        else -> TODO()
    }
}