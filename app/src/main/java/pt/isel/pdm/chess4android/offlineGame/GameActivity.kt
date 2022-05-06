package pt.isel.pdm.chess4android.offlineGame

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.databinding.GameActivityBinding
import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.GameState



class GameActivity : AppCompatActivity() {

    private val binding by lazy {
        GameActivityBinding.inflate(layoutInflater)
    }



    private val viewModel: ChessViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var game: GameState? = viewModel.gameState.value

        binding.moveInfo.visibility = View.GONE
        binding.cardTitle.text = resources.getString(R.string.offline_game_title)


        if (game == GameState.SETUP_MAP)
            viewModel.setChessBoard()


        viewModel.lastMove.observe(this) {
            binding.boardView.updatePiecePressed(it)
        }

        viewModel.gameState.observe(this) {
            game = GameState.valueOf(it.toString())
            binding.boardView.gameState = it
            when (it) {
                GameState.SETUP_MAP -> {
                    viewModel.setChessBoard()
                }

                GameState.GAME_FINISHED -> {
                    onFinish()
                }

            }
        }

        viewModel.lastMove.observe(this) {
            binding.boardView.updatePiecePressed(it)
        }


        viewModel.isPlayerInCheck.observe(this) {
            val king: Int
            val army: String

            if (it == Army.WHITE) {
                king = viewModel.board.blackKingPosition
                army = resources.getString(R.string.white)
            } else {
                army = resources.getString(R.string.black)
                king = viewModel.board.whiteKingPosition
            }
            binding.checkText.text = resources.getString(R.string.check_mate_message, army)

            binding.boardView.kingInCheck(king)

        }


        viewModel.isOpponentInCheck.observe(this) {
            val army = if (it == Army.WHITE)
                resources.getString(R.string.white)
            else resources.getString(R.string.black)
            if (it == null) {
                binding.checkText.text = ""
            } else
                binding.checkText.text = resources.getString(R.string.check_message, army)

        }


        viewModel.currPieceTouchedMoves.observe(this) {
            binding.boardView.updateMoves(it)
        }

        viewModel.turn.observe(this) {
            val turn =
                if (it.equals(Army.BLACK)) getString(R.string.turn_black) else getString(R.string.turn_white)
            binding.turnText.text = turn
            if (game?.name != GameState.WAITING.name)
                viewModel.reverseBoard()
        }


        binding.boardView.onTileClickedListener = { row: Int, column: Int ->
            if (game == GameState.WAITING)
                viewModel.pieceTouched(row, column)
            else if (game == GameState.PIECE_TOUCHED) viewModel.movePieceIfPossible(row, column)
        }


        viewModel.chessBoard.observe(this) {
            binding.boardView.updateBoard(it)
        }
    }

    private fun onFinish() {

    }
}