package pt.isel.pdm.chess4android.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.GridLayout
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.model.*
import pt.isel.pdm.chess4android.views.Tile.Type


typealias TileTouchListener = (row: Int, column: Int) -> Unit

/**
 * Custom view that implements a chess board.
 */
@SuppressLint("ClickableViewAccessibility")

class BoardView(private val ctx: Context, attrs: AttributeSet?) :
    GridLayout(ctx, attrs) {


    var gameState: GameState? = null
    var onTileClickedListener: TileTouchListener? = null

    private val side = 8

    var boardObj = Board(side, side)

    var board: HashMap<Int, Pair<Army, Piece>?> = boardObj.squaresMap
        set(value) {
            field = value
            super.invalidate()
        }

    var tileMap: HashMap<Int, Tile>? = hashMapOf()

    private val brush = Paint().apply {
        ctx.resources.getColor(R.color.chess_board_black, null)
        style = Paint.Style.STROKE
        strokeWidth = 10F
    }

    private fun createImageEntry(army: Army, piece: Piece, imageId: Int) =
        Pair(Pair(army, piece), VectorDrawableCompat.create(ctx.resources, imageId, null))

    private val piecesImages = mapOf(
        createImageEntry(Army.WHITE, Piece.PAWN, R.drawable.ic_white_pawn),
        createImageEntry(Army.WHITE, Piece.KNIGHT, R.drawable.ic_white_knight),
        createImageEntry(Army.WHITE, Piece.BISHOP, R.drawable.ic_white_bishop),
        createImageEntry(Army.WHITE, Piece.ROOK, R.drawable.ic_white_rook),
        createImageEntry(Army.WHITE, Piece.QUEEN, R.drawable.ic_white_queen),
        createImageEntry(Army.WHITE, Piece.KING, R.drawable.ic_white_king),
        createImageEntry(Army.BLACK, Piece.MOVE, R.drawable.move_white),
        createImageEntry(Army.BLACK, Piece.PAWN, R.drawable.ic_black_pawn),
        createImageEntry(Army.BLACK, Piece.KNIGHT, R.drawable.ic_black_knight),
        createImageEntry(Army.BLACK, Piece.BISHOP, R.drawable.ic_black_bishop),
        createImageEntry(Army.BLACK, Piece.ROOK, R.drawable.ic_black_rook),
        createImageEntry(Army.BLACK, Piece.QUEEN, R.drawable.ic_black_queen),
        createImageEntry(Army.BLACK, Piece.KING, R.drawable.ic_black_king),
        createImageEntry(Army.WHITE, Piece.MOVE, R.drawable.move_black),

        )

    init {

        boardObj.loadStandardStartingPosition()
        rowCount = side
        columnCount = side

        if (tileMap?.isEmpty() == true) {
            repeat(side * side) {
                val row = it / side
                val column = it % side

                val tile = Tile(
                    ctx,
                    if ((row + column) % 2 == 0) Type.WHITE else Type.BLACK,
                    side,
                    piecesImages,
                    board[row * side + column]
                )
                tileMap?.set(it, tile)
                tile.setOnClickListener { onTileClickedListener?.invoke(row, column) }
                addView(tile)
            }
        }
    }


    fun updateBoard(newBoard: HashMap<Int, Pair<Army, Piece>?>) {
        for ((key) in newBoard) {
            tileMap?.get(key)?.piece = newBoard[key]
        }
    }

    fun updatePiecePressed(move: Pair<Square?, Square?>?) {
        resetBoardTypes()
        val source = move?.first
        val destination = move?.second
        if (source != null)
            tileMap?.get(source)?.type = Type.MOVE_SOURCE
        if (destination != null)
            tileMap?.get(destination)?.type = Type.MOVE_DESTINATION
    }


    override fun dispatchDraw(canvas: Canvas) {
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, brush)
        super.dispatchDraw(canvas)
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), brush)
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), brush)
        canvas.drawLine(width.toFloat(), 0f, width.toFloat(), height.toFloat(), brush)
    }

    private fun resetBoardTypes() {
        for ((key) in tileMap!!) {
            val row = key / side
            val column = key % side
            tileMap?.get(key)?.type = if ((row + column) % 2 == 0) Type.WHITE else Type.BLACK
            invalidate()
        }
    }

    fun updateMoves(moves: MutableList<Square>?) {
        if (moves != null) {
            if (moves.isNotEmpty())
                moves.iterator().forEach {

                    val piece = tileMap?.get(it)

                    if (piece?.piece == null)
                        if (piece?.type == Type.WHITE)
                            tileMap?.get(it)?.piece = Pair(Army.WHITE, Piece.MOVE)
                        else
                            tileMap?.get(it)?.piece = Pair(Army.BLACK, Piece.MOVE)


                    else
                        tileMap?.get(it)?.type = Type.CAPTURE
                }
        }
    }

    fun kingInCheck(kingPosition: Int) {

        tileMap?.get(kingPosition)?.type = Type.CHECK

    }
}