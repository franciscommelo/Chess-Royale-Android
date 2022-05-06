package pt.isel.pdm.chess4android.model

import pt.isel.pdm.chess4android.model.moves.move

fun Board.importMovesFromPGN(pgn: String) {
    reset()
    loadStandardStartingPosition()
    val lines = pgn.split("\n")
    for (line in lines) {
        if (line.trim().isBlank()) {
            continue
        } else {
            val sans = line.split(" ").filter { !it.contains(".") && it.isNotBlank() }
            sans.forEach {
                    move(sanToMove(it.trim()))
            }
        }
    }
}