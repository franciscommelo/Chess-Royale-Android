package pt.isel.pdm.chess4android.offlineGame

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import pt.isel.pdm.chess4android.model.*
import pt.isel.pdm.chess4android.model.moves.canPieceMoveFrom
import pt.isel.pdm.chess4android.model.moves.getPossibleMovesFrom
import pt.isel.pdm.chess4android.model.moves.isOnCheck
import pt.isel.pdm.chess4android.model.moves.move
import pt.isel.pdm.chess4android.onlineGame.GAME_STATE_KEY


class ChessViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {

    val gameState: MutableLiveData<GameState> = MutableLiveData(GameState.SETUP_MAP)

    val turn: MutableLiveData<Army> = MutableLiveData(Army.WHITE)


    val isPlayerInCheck: MutableLiveData<Army?> = MutableLiveData()
    val isOpponentInCheck: MutableLiveData<Army?> = MutableLiveData()


    val board = Board(8, 8)

    private var pState = savedStateHandle.get<BlockState>(GAME_STATE_KEY)
        set(value) {
            field = value
            savedStateHandle[GAME_STATE_KEY] = value
            chessBoard.value = value?.board?.squaresMap
            gameState.value = value?.gameState
            turn.value = value?.turn
            lastMove.value = value?.lastMove
            currPieceTouchedMoves.value = value?.currPieceTouchedMoves
        }

    val chessBoard: MutableLiveData<HashMap<Int, Pair<Army, Piece>?>> =
        MutableLiveData(pState?.board?.squaresMap)

    val lastMove: MutableLiveData<Pair<Square?, Square?>?> = MutableLiveData()

    val currPieceTouchedMoves: MutableLiveData<MutableList<Square>?> =
        MutableLiveData(mutableListOf())

    fun setChessBoard() {
        //    if (pState?.board?.squaresMap?.isEmpty() == true)
        board.loadStandardStartingPosition()
        chessBoard.value = board.squaresMap
        turn.value = board.turn
        gameState.value = GameState.WAITING
    }


    fun movePieceIfPossible(row: Int, column: Int) {

        if (board.canPieceMoveFrom(board.currPieceTouches!!, board.square(row, column))) {
            board.move(board.getPossibleMovesFrom(board.currPieceTouches!!, false).first {
                it.destination == board.square(row, column)
            })
        }

        currPieceTouchedMoves.value?.clear()
        currPieceTouchedMoves.value = currPieceTouchedMoves.value


        lastMove.value = Pair(null, null)
        chessBoard.value = board.squaresMap

        val opponent = if (turn.value == Army.WHITE) Army.BLACK else Army.WHITE

        // check if the player has left own king in check
        if (board.isOnCheck(opponent)) {
            isPlayerInCheck.value = opponent
            gameState.value = GameState.GAME_FINISHED
        } else {

            // check if the opponent is in check
            if (board.isOnCheck(turn.value!!)) {
                isOpponentInCheck.value = board.turn
            }else{
                isOpponentInCheck.value = null
            }

            if (turn.value != board.turn)
                turn.value = board.turn

            if (gameState.value == GameState.PIECE_TOUCHED)
                gameState.value = GameState.WAITING

        }
    }


    fun pieceTouched(row: Int, column: Int) {

        val square = board.square(row, column)

        val moves: MutableList<Square> = mutableListOf()

        if (!board.squareEmpty(square) && board.squaresMap[square]?.first == board.turn) {


            for (move in board.getPossibleMovesFrom(square, false)) {
                moves.add(move.destination)
            }
            board.currPieceTouches = square
            gameState.value = GameState.PIECE_TOUCHED
            chessBoard.value = board.squaresMap
            lastMove.value = Pair(board.currPieceTouches, null)
            currPieceTouchedMoves.value = moves
        }
    }


    fun reverseBoard() {
        Handler(Looper.getMainLooper()).postDelayed({
            val newBoard: HashMap<Int, Pair<Army, Piece>?> = hashMapOf()
            var key = 0

            for (value in board.squaresMap.toSortedMap(Comparator.reverseOrder()).values) {
                newBoard[key++] = value
            }

            board.whiteKingPosition = 63 - board.whiteKingPosition
            board.blackKingPosition = 63 - board.blackKingPosition

            board.squaresMap = newBoard
            chessBoard.value = board.squaresMap

        }, 800)
    }

}





