package pt.isel.pdm.chess4android.onlineGame

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pt.isel.pdm.chess4android.ChessApplication
import pt.isel.pdm.chess4android.model.*
import pt.isel.pdm.chess4android.model.moves.canPieceMoveFrom
import pt.isel.pdm.chess4android.model.moves.getPossibleMovesFrom
import pt.isel.pdm.chess4android.model.moves.isOnCheck
import pt.isel.pdm.chess4android.model.moves.move
import pt.isel.pdm.chess4android.repo.ChessGameState


class OnlineChessViewModel(
    application: Application,
    private val initialGameState: ChessGameState,
) : AndroidViewModel(application) {


    val gameState: MutableLiveData<GameState> = MutableLiveData(GameState.SETUP_MAP)

    private val _game: MutableLiveData<Result<ChessGameState>> by lazy {
        MutableLiveData(Result.success(initialGameState))
    }


    val game: LiveData<Result<ChessGameState>> = _game

    val turn: MutableLiveData<Army> = MutableLiveData(Army.WHITE)

    val isPlayerInCheck: MutableLiveData<Army?> = MutableLiveData()
    val isOpponentInCheck: MutableLiveData<Army?> = MutableLiveData()

    val board = Board(8, 8)


    private val gameSubscription = getApplication<ChessApplication>()
        .gameRepository.subscribeToGameStateChanges(
            challengeId = initialGameState.id,
            onGameStateChange = {
                _game.value =
                    Result.success(ChessGameState(it.id,
                        it.turn,
                        it.boardFEN,
                        it.lastMove,
                        it.isCheck,
                        it.isCheckMate,
                        it.isWithdrawn
                    ))
            },
            onSubscriptionError = { _game.value = Result.failure(it) }
        )


    val chessBoard: MutableLiveData<HashMap<Int, Pair<Army, Piece>?>> =
        MutableLiveData()

    val lastMove: MutableLiveData<Pair<Square?, Square?>?> = MutableLiveData()

    val currPieceTouchedMoves: MutableLiveData<MutableList<Square>?> =
        MutableLiveData(mutableListOf())


    fun movePieceIfPossible(row: Int, column: Int) {

        var currMove: Pair<Square?, Square?>? = null

        if (board.canPieceMoveFrom(board.currPieceTouches!!, board.square(row, column))) {
            board.move(board.getPossibleMovesFrom(board.currPieceTouches!!, false).first {
                it.destination == board.square(row, column)
            })
            currMove = Pair(63 - board.currPieceTouches!!, 63 - board.square(row, column))
        }

        currPieceTouchedMoves.value?.clear()
        currPieceTouchedMoves.value = currPieceTouchedMoves.value

        val boardFen = board.boardToFEN()

        lastMove.value = Pair(null, null)
        chessBoard.value = board.squaresMap

        val opponent = if (turn.value == Army.WHITE) Army.BLACK else Army.WHITE


        if (currMove != null) {
            // check if the player has left own king in check
            if (board.isOnCheck(opponent)) {
                isPlayerInCheck.value = opponent
                gameState.value = GameState.GAME_FINISHED
            }

            // check if the opponent is in check
            if (board.isOnCheck(turn.value!!)) {
                isOpponentInCheck.value = board.turn
            } else {
                isOpponentInCheck.value = null
            }
        }

        if (turn.value != board.turn) {
            turn.value = board.turn

            game.value?.onSuccess { board ->
                _game.value = Result.success(ChessGameState(initialGameState.id,
                    turn.value!!.name,
                    boardFEN = boardFen!!,
                    lastMove = currMove.toString(),
                    isCheck = isOpponentInCheck.value,
                    isCheckMate = isPlayerInCheck.value,
                    isWithdrawn = false
                ))

                getApplication<ChessApplication>().gameRepository.updateGameState(
                    chessGameState = ChessGameState(initialGameState.id,
                        turn = turn.value!!.name,
                        boardFEN = boardFen,
                        lastMove = currMove.toString(),
                        isCheck = isOpponentInCheck.value,
                        isCheckMate = isPlayerInCheck.value,
                        isWithdrawn = false
                    ),
                    onComplete = { result ->
                        result.onFailure { _game.value = Result.failure(it) }
                    }
                )
            }
        }
        if (gameState.value == GameState.PIECE_TOUCHED)
            gameState.value = GameState.WAITING
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


    override fun onCleared() {
        super.onCleared()
        getApplication<ChessApplication>().gameRepository.deleteGame(
            challengeId = initialGameState.id,
            onComplete = { }
        )

        game.value?.onSuccess { board ->
            _game.value = Result.success(ChessGameState(initialGameState.id,
                turn.value!!.name,
                boardFEN = board.boardFEN,
                lastMove = board.lastMove,
                isOpponentInCheck.value,
                isPlayerInCheck.value,
                true
            ))

            getApplication<ChessApplication>().gameRepository.updateGameState(
                chessGameState = ChessGameState(initialGameState.id,
                    turn.value!!.name,
                    boardFEN = board.boardFEN,
                    lastMove = board.lastMove.toString(),
                    isOpponentInCheck.value,
                    isPlayerInCheck.value,
                    true
                ),
                onComplete = { result ->
                    result.onFailure { _game.value = Result.failure(it) }
                }
            )
        }
        gameSubscription.remove()
    }


    fun updateChessBoard(fen: String?, newTurn: String?, check: Army?, checkMate: Army?) {

        if (fen != null)
            board.loadPositionFromFen(fen)
        chessBoard.value = board.squaresMap

        if (check != null)
            isOpponentInCheck.value = check
        else
            if (checkMate != null)
                isPlayerInCheck.value = checkMate

        if (newTurn != null)
            board.turn = Army.valueOf(newTurn)
        turn.value = board.turn
        gameState.value = GameState.WAITING
    }
}





