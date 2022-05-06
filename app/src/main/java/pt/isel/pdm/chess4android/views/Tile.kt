package pt.isel.pdm.chess4android.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.Piece

/**
 * Custom view that implements a chess board tile.
 * Tiles are either black or white and can they can be empty or occupied by a chess piece.
 *
 * Implementation note: This view is not to be used with the designer tool.
 * You need to adapt this view to suit your needs. ;)
 *
 * @property type           The tile's type (i.e. black or white)
 * @property tilesPerSide   The number of tiles in each side of the chess board
 *
 */

@SuppressLint("ViewConstructor")
class Tile(
    private val ctx: Context,
    type: Type,
    private val tilesPerSide: Int,
    private val images: Map<Pair<Army, Piece>, VectorDrawableCompat?>,
    initialPiece: Pair<Army, Piece>? = null,
) : View(ctx) {

    var piece: Pair<Army, Piece>? = initialPiece
        set(value) {
            field = value
            invalidate()
        }

    var type: Type = type
        set(value) {
            field = value
            invalidate()
        }


    enum class Type { WHITE, BLACK, MOVE_SOURCE, MOVE_DESTINATION, POSSIBLE_MOVE, CAPTURE, CHECK }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val side = Integer.min(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        )
        setMeasuredDimension(side / tilesPerSide, side / tilesPerSide)
    }


    override fun onDraw(canvas: Canvas) {

        val brush = Paint().apply {
            val colorID = when (type) {
                Type.MOVE_SOURCE -> R.color.move_source
                Type.MOVE_DESTINATION -> R.color.move_destination
                Type.BLACK -> R.color.chess_board_white
                Type.WHITE -> R.color.chess_board_black
                Type.POSSIBLE_MOVE -> R.color.possible_move
                Type.CAPTURE -> R.color.capture
                Type.CHECK -> R.color.red
            }
            color = ctx.resources.getColor(
                colorID
            )
            style = Paint.Style.FILL_AND_STROKE
        }

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), brush)
        if (piece != null) {
            images[piece]?.apply {
                val padding = 8
                setBounds(padding, padding, width - padding, height - padding)
                draw(canvas)
            }
        }
    }
}
