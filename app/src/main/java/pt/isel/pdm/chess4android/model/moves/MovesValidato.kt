package pt.isel.pdm.chess4android.model.moves

import pt.isel.pdm.chess4android.model.*


fun Board.row(square: Square) = square / columns
fun Board.column(square: Square) = square % columns
fun Board.sum(square: Square) = row(square) + column(square)
fun Board.diff(square: Square) = row(square) - column(square)


fun Board.areSquaresOnSameRow(a: Square, b: Square): Boolean {
    return row(a) == row(b)
}

fun Board.areSquaresOnSameColumn(a: Square, b: Square): Boolean {
    return column(a) == column(b)
}

fun Board.areSquaresOnSameDiagonal(a: Square, b: Square): Boolean {
    return diff(a) == diff(b) || sum(a) == sum(b)
}

fun Board.areSquaresClearOnSameRow(a: Square, b: Square): Boolean {
    return areSquaresOnSameRow(a, b)
            &&
            squaresBetweenSquaresOnRow(a, b).all {
                squaresMap[it] == null
            }
}

fun Board.areSquaresClearOnSameColumn(a: Square, b: Square): Boolean {
    return areSquaresOnSameColumn(a, b)
            &&
            squaresBetweenSquaresOnColumn(a, b).all {
                squaresMap[it] == null
            }
}

fun Board.areSquaresClearOnSameDiagonal(a: Square, b: Square): Boolean {
    return (areSquaresOnSameDiagonal(a, b))
            &&
            squaresBetweenSquaresOnDiagonal(a, b).all {
                squaresMap[it] == null
            }
}

fun Board.squaresBetweenSquaresOnRow(a: Square, b: Square): IntArray {
    if (!areSquaresOnSameRow(a, b))
        throw IllegalArgumentException("The two squares must be in the same row!")

    return (minOf(a, b) + 1 until maxOf(a, b)).toList().toIntArray()
}

fun Board.squaresBetweenSquaresOnColumn(a: Square, b: Square): IntArray {
    if (!areSquaresOnSameColumn(a, b))
        throw IllegalArgumentException("The two squares must be in the same column!")

    return ((minOf(row(a), row(b)) + 1) until maxOf(row(a), row(b))).toList().map { row ->
        square(row, column(a))
    }.toIntArray()
}

fun Board.squaresBetweenSquaresOnDiagonal(a: Square, b: Square): IntArray {

    when {
        sum(a) == sum(b) -> {
            return ((minOf(row(a), row(b)) + 1) until maxOf(row(a), row(b))).toList().map { row ->
                val column = sum(a) - row
                (row * columns) + column
            }.toIntArray()
        }
        diff(a) == diff(b) -> {
            return ((minOf(row(a), row(b)) + 1) until maxOf(row(a), row(b))).toList().map { row ->
                val column = row - diff(a)
                (row * columns) + column
            }.toIntArray()
        }
        else -> throw IllegalArgumentException("The two squares must be in the same diagonal!")
    }

}

fun Board.squaresWithPiecesColored(color: Army): List<Int> {
    return squaresMap.filter { it ->
        it.value.let {
            it != null && it.first == color
        }
    }.map { it.key }
}

fun Board.squaresWithPiecesColoredAndType(color: Army, type: Piece): List<Int> {
    return squaresMap.filter {
        it.value.let {
            it != null && it.first == color && it.second == type
        }
    }.map { it.key }
}

fun Board.coordinateToSquare(coo: String): Square {
    if (coo.length != 2) throw java.lang.Exception("Invalid coordinates size")
    return (rows - coo[1].digitToInt()) * rows + fileToColumn(coo[0].toString())
}

fun Board.squareToCoordinates(source: Square, destination: Square): String {
    return "${columnToFile(source % rows)}${rows - (source / rows)}${columnToFile(destination % rows)}${rows - (destination / rows)}"
}