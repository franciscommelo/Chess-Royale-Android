package pt.isel.pdm.chess4android.puzzle

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.isel.pdm.chess4android.ChessApplication
import pt.isel.pdm.chess4android.model.*
import pt.isel.pdm.chess4android.model.moves.getPossibleMovesFrom
import pt.isel.pdm.chess4android.model.moves.moveFromCoordinates
import pt.isel.pdm.chess4android.model.moves.squareToCoordinates
import pt.isel.pdm.chess4android.service.DailyPuzzleService.PuzzleDTO


class GameViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {

    private val chessApplication: ChessApplication by lazy { getApplication() }

    val gameState: MutableLiveData<GameState> = MutableLiveData(GameState.SETUP_MAP)

    private val solutionMovementNumber: MutableLiveData<Int> = MutableLiveData(0)

    private val puzzlePgn: MutableLiveData<String> = MutableLiveData()

    private val puzzleDTO: MutableLiveData<PuzzleDTO> = MutableLiveData()

    private val solutionOpponent: MutableLiveData<MutableList<String>> = MutableLiveData()

    val solutionMove: MutableLiveData<MutableList<String>> = MutableLiveData()

    val turn: MutableLiveData<Army> = MutableLiveData()

    val board = Board(8, 8)

    private val gameRepository by lazy { chessApplication.puzzleRepository }

    val currPieceTouchedMoves: MutableLiveData<MutableList<Square>?> =
        MutableLiveData(mutableListOf())

    private var pState = savedStateHandle.get<BlockState>(BOARD_STATE_KEY)
        set(value) {
            field = value
            savedStateHandle[BOARD_STATE_KEY] = value
            chessBoard.value = value?.board?.squaresMap
            puzzlePgn.value = value?.puzzle
            solutionMove.value = value?.solutionMove
            solutionOpponent.value = value?.solutionOpponent
            gameState.value = value?.gameState
            turn.value = value?.turn
            lastMove.value = value?.lastMove
        }

    val chessBoard: MutableLiveData<HashMap<Int, Pair<Army, Piece>?>> =
        MutableLiveData(pState?.board?.squaresMap)

    val lastMove: MutableLiveData<Pair<Square?, Square?>?> = MutableLiveData()

    fun setChessBoard() {
        if (pState?.board?.squaresMap?.isEmpty() == true)
            board.loadStandardStartingPosition()
        chessBoard.value = board.squaresMap
    }

    fun moveSolution(solution: List<String>) {
        if (solutionMovementNumber.value!! < (solution.size)) {
            board.moveFromCoordinates(solution[solutionMovementNumber.value!!])
            chessBoard.value = board.squaresMap
            lastMove.value = board.lastMove
            solutionMovementNumber.value = solutionMovementNumber.value!!.inc()
            gameState.value = GameState.SHOW_SOLUTION
        } else {
            gameState.value = GameState.PUZZLE_FINISHED
            solutionMovementNumber.value = 0
        }
    }


    fun startPuzzle(png: String, solution: List<String>, puzzleDto: PuzzleDTO) {

        if (pState?.puzzle == null) {
            puzzlePgn.value = png
            solutionMove.value = mutableListOf()
            solutionOpponent.value = mutableListOf()
            puzzleDTO.value = puzzleDto
            for (j in solution.indices) {
                if (j % 2 == 0) {
                    solutionMove.value?.add(solution[j])
                } else {
                    solutionOpponent.value?.add(solution[j])
                }
                solutionMove.value = solutionMove.value
            }
        }

        setPuzzleBoard(pgn = puzzlePgn.value!!)
        if (board.turn == Army.BLACK) {
            reverseBoard()
            reversePuzzle(solutionMove.value, solutionOpponent.value)
        }

        if (puzzleDto.isShowSolution == true) gameState.value = GameState.SHOW_SOLUTION
    }


    private fun setPuzzleBoard(pgn: String) {
        board.turn = Army.WHITE
        board.importMovesFromPGN(pgn)
        chessBoard.value = board.squaresMap
        gameState.value = GameState.WAITING
        turn.value = board.turn
    }


    private fun reverseBoard() {
        val newBoard: HashMap<Int, Pair<Army, Piece>?> = hashMapOf()
        var key = 0
        for (value in board.squaresMap.toSortedMap(Comparator.reverseOrder()).values) {
            newBoard[key++] = value
        }
        board.squaresMap = newBoard
        chessBoard.value = board.squaresMap
    }


    fun movePieceIfPossible(row: Int, column: Int) {
        val sourceCoo =
            board.squareToCoordinates(board.currPieceTouches!!, board.square(row, column))
        val currSolutionMove = solutionMove.value?.first()
        if (sourceCoo == currSolutionMove) {
            board.moveFromCoordinates(solutionMove.value?.first()!!)
            solutionMove.value!!.removeFirst()
            solutionMove.value = solutionMove.value
            lastMove.value = board.lastMove
            puzzleOpponentMove()
        } else {
            board.currPieceTouches = null
            lastMove.value = Pair(null, null)
        }

        chessBoard.value = board.squaresMap

        if (gameState.value == GameState.PIECE_TOUCHED)
            gameState.value = GameState.WAITING
    }

    private fun puzzleOpponentMove() {

        if (solutionMove.value?.size == 0) {
            board.currPieceTouches = null
            lastMove.value = Pair(null, null)

            viewModelScope.launch {
                gameRepository.setPuzzleHasFinished(puzzleDTO.value!!)
            }

            gameState.value = GameState.PUZZLE_FINISHED

        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                val nextOpponentMove = solutionOpponent.value?.first()
                if (nextOpponentMove != null)
                    board.moveFromCoordinates(nextOpponentMove)
                solutionOpponent.value?.removeFirst()
                solutionOpponent.value = solutionOpponent.value
                chessBoard.value = board.squaresMap
                lastMove.value = board.lastMove
            }, 1000) // wait 1s to move opponent piece
        }
    }


    fun pieceTouched(row: Int, column: Int) {

        val square = board.square(row, column)
        val moves: MutableList<Square> = mutableListOf()

        if (gameState.value != GameState.PUZZLE_FINISHED) {

            for (move in board.getPossibleMovesFrom(square, false)) {
                moves.add(move.destination)
            }


            if (board.getSquareOccupantAsChessPiece(board.square(row,
                    column))?.first == board.turn
            ) {
                board.currPieceTouches = board.square(row, column)
                gameState.value = GameState.PIECE_TOUCHED
                lastMove.value = Pair(board.currPieceTouches, null)
                currPieceTouchedMoves.value = moves

            }
            chessBoard.value = board.squaresMap
        }
    }

    fun resetPuzzle() {
        gameState.value = GameState.RESET_SOLUTION
    }

    fun getSolutionText(): String {
        return solutionMove.value.toString()
    }


    private fun reversePuzzle(
        playerMove: MutableList<String>?,
        opponentMove: MutableList<String>?,
    ) {

        val newSolutionMove: MutableList<String> = mutableListOf()

        for (move in playerMove!!) {
            newSolutionMove.add(reverseCoordinates(move))
        }

        val newSolutionOpponent: MutableList<String> = mutableListOf()

        for (move in opponentMove!!) {
            newSolutionOpponent.add(reverseCoordinates(move))
        }

        solutionOpponent.value = newSolutionOpponent
        solutionMove.value = newSolutionMove
    }


    fun invertPuzzleSolution(moves: List<String>): List<String>? {

        val newSolutionMove: MutableList<String> = mutableListOf()

        for (move in moves) {
            newSolutionMove.add(reverseCoordinates(move))
        }

        return newSolutionMove

    }


}
