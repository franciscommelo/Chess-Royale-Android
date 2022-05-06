package pt.isel.pdm.chess4android.onlineGame

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.challenges.ChallengeInfo
import pt.isel.pdm.chess4android.challenges.model.Player
import pt.isel.pdm.chess4android.databinding.GameActivityBinding
import pt.isel.pdm.chess4android.menu.MenuActivity
import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.GameState
import pt.isel.pdm.chess4android.repo.ChessGameState

const val GAME_STATE_KEY = "GAME_STATE"
const val LOCAL_PLAYER_EXTRA = "LOCAL_PLAYER_EXTRA"

class OnlineChessActivity : AppCompatActivity() {

    private val binding by lazy {
        GameActivityBinding.inflate(layoutInflater)
    }


    companion object {
        fun buildIntent(
            origin: Context,
            local: Player,
            turn: Player,
            boardFEN: String,
            lastMove: String?,
            challengeInfo: ChallengeInfo,
        )
                : Intent {
            return Intent(origin, OnlineChessActivity::class.java)
                .putExtra(GAME_STATE_KEY,
                    ChessGameState(challengeInfo.id,
                        turn.name,
                        boardFEN = boardFEN,
                        lastMove = lastMove,
                        isCheckMate = null,
                        isCheck = null,
                        isWithdrawn = false
                    ))
                .putExtra(LOCAL_PLAYER_EXTRA, local.name)
        }
    }

    private val localPlayer: Player by lazy {
        val local = intent.getStringExtra(LOCAL_PLAYER_EXTRA)
        if (local != null) Player.valueOf(local)
        else throw IllegalArgumentException("Mandatory extra $LOCAL_PLAYER_EXTRA not present")
    }

    private val initialState: ChessGameState by lazy {
        intent.getParcelableExtra<ChessGameState>(GAME_STATE_KEY) ?: throw IllegalArgumentException(
            "Mandatory extra $GAME_STATE_KEY not present")
    }

    private val viewModel: OnlineChessViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return OnlineChessViewModel(application, initialState) as T
            }
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.cardTitle.text = resources.getString(R.string.online_game_title)

        viewModel.game.observe(this) {
            it.onSuccess { it ->

                if (it.isWithdrawn) {
                    MaterialAlertDialogBuilder(this)
                        .setMessage(resources.getString(R.string.opponent_left_game))
                        .setNegativeButton(R.string.go_to_menu) { dialog, which ->
                            val intent = Intent(this, MenuActivity::class.java)
                            startActivity(intent)
                        }
                        .show()
                } else {
                    if (it.turn == localPlayer.name || it.isCheckMate != null) {
                        binding.animationView.visibility = View.GONE
                        binding.moveInfoText.text = resources.getString(R.string.your_turn)
                        viewModel.updateChessBoard(it.boardFEN, it.turn, it.isCheck, it.isCheckMate)

                        if (it.lastMove != null) {
                            var moves = it.lastMove.removePrefix("(").removeSuffix(")").split(",")
                            binding.boardView.updatePiecePressed(Pair(moves.first().toInt(),
                                moves.last().removePrefix(" ").toInt()))
                        }

                    } else {
                        binding.animationView.visibility = View.VISIBLE
                        binding.moveInfoText.text = resources.getString(R.string.waiting_for_move)
                    }
                }
            }
        }


        var game: GameState? = viewModel.gameState.value

        viewModel.gameState.observe(this) {
            game = GameState.valueOf(it.toString())
        }

        viewModel.lastMove.observe(this) {
            binding.boardView.updatePiecePressed(it)
        }

        viewModel.isPlayerInCheck.observe(this) {

            val king: Int
            val army: String

            if (it == Army.WHITE) {
                king = viewModel.board.getBlackKingPosition()!!
                army = resources.getString(R.string.white)
            } else {
                army = resources.getString(R.string.black)
                king = viewModel.board.getWhiteKingPosition()!!
            }

            binding.checkText.text = resources.getString(R.string.check_mate_message, army)
            binding.moveInfoText.text = ""
            binding.boardView.kingInCheck(king)


            var message: String
            var color: Int
            var img: Int

            if (it!!.name == localPlayer.name) {

                message = resources.getString(R.string.won_message)
                color = resources.getColor(R.color.capture)
                img = R.drawable.win

            } else {

                message = resources.getString(R.string.lost_message)
                color = resources.getColor(R.color.newChallenge)
                img = R.drawable.lose

            }

            binding.moveInfoText.text = message
            binding.moveInfoText.setTextColor(color)
            val toast = Toast(this)
            val view = ImageView(applicationContext)
            view.setImageResource(img);
            toast.duration = Toast.LENGTH_SHORT
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.view = view;
            toast.show()
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
        }


        binding.boardView.onTileClickedListener = { row: Int, column: Int ->
            if (game == GameState.WAITING && viewModel.turn.value?.name == localPlayer.name && viewModel.isPlayerInCheck.value == null)
                viewModel.pieceTouched(row, column)
            else if (game == GameState.PIECE_TOUCHED) viewModel.movePieceIfPossible(row, column)
        }


        viewModel.chessBoard.observe(this) {
            binding.boardView.updateBoard(it)
        }
    }


    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.leaving_game_title))
            .setMessage(resources.getString(R.string.leaving_game_message))
            .setNeutralButton(resources.getString(R.string.cancel_btn)) { dialog, which ->

            }
            .setPositiveButton(resources.getString(R.string.go_to_menu)) { dialog, which ->
                super.onBackPressed()
            }
            .show()

    }


}