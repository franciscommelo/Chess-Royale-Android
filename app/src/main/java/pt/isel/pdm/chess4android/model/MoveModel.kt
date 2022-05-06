package pt.isel.pdm.chess4android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class Move  {
    abstract val source: Square
    abstract val destination: Square
    var isCapture: Boolean = false
    var san: String = ""
}

@Parcelize
data class RegularMove(
    override val source: Square,
    override val destination: Square
) : Move(), Parcelable

@Parcelize
data class Promotion(
    override val source: Square,
    override val destination: Square,
    var promotionType: Piece
) : Move(), Parcelable


@Parcelize
data class EnPassant(
    override val source: Square,
    override val destination: Square
) : Move(), Parcelable

@Parcelize
data class Castling(
    override val source: Square,
    override val destination: Square
) : Move(), Parcelable