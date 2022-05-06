package pt.isel.pdm.chess4android.puzzle

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.databinding.ActivityMainBinding
import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.GameState
import pt.isel.pdm.chess4android.service.DailyPuzzleService.PuzzleDTO


const val BOARD_STATE_KEY = "BOARD_STATE"
private const val Puzzle_EXTRA = "Puzzle.Extra.puzzle"

class MainActivity : AppCompatActivity() {

    companion object {
        fun buildIntent(origin: Activity, puzzleDTO: PuzzleDTO): Intent {
            val msg = Intent(origin, MainActivity::class.java)
            msg.putExtra(Puzzle_EXTRA, puzzleDTO)
            return msg
        }
    }

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel: GameViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val puzzle = intent.getParcelableExtra<PuzzleDTO>(Puzzle_EXTRA)

        var game: GameState? = viewModel.gameState.value


        viewModel.gameState.observe(this) {
            game = GameState.valueOf(it.toString())
            binding.boardView.gameState = it
            when (it) {
                GameState.SETUP_MAP -> {
                    viewModel.setChessBoard()
                    binding.boardView.updatePiecePressed(null)
                    viewModel.startPuzzle(puzzle?.pgn!!, puzzle.solution, puzzle)

                    if(puzzle.isShowSolution == true && viewModel.board.turn == Army.BLACK){
                        puzzle.solution = viewModel.invertPuzzleSolution(puzzle.solution)!!
                    }

                    binding.rotateButton.visibility = GONE
                }

                GameState.PUZZLE_FINISHED -> {
                    binding.moveInfoText.text = getString(R.string.puzzle_finished)
                    binding.rotateButton.visibility = VISIBLE
                }

                GameState.SHOW_SOLUTION -> {
                    //val solutionString : String = puzzle?.solution.toString()
                    binding.cardTitle.text = getString(R.string.showing_solution)
                    binding.turnText.visibility = GONE
                    Handler(Looper.getMainLooper()).postDelayed({
                        viewModel.moveSolution(puzzle!!.solution)},1000)
                    }

                GameState.RESET_SOLUTION -> {
                    viewModel.setChessBoard()
                    binding.boardView.updatePiecePressed(null)
                    viewModel.startPuzzle(puzzle?.pgn!!, puzzle.solution, puzzle)
                    binding.rotateButton.visibility = GONE

                }

            }
        }

        viewModel.lastMove.observe(this) {
            binding.boardView.updatePiecePressed(it)
        }

        viewModel.chessBoard.observe(this) {
            binding.boardView.updateBoard(it)
        }


        viewModel.currPieceTouchedMoves.observe(this) {
            binding.boardView.updateMoves(it)
        }

        binding.rotateButton.setOnClickListener { viewModel.resetPuzzle() }

        viewModel.solutionMove.observe(this) {
            binding.moveInfoText.text = "${getString(R.string.moves_left)} ${it.size}"
        }

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.show_solution_btn -> {
                    binding.solutionText?.text = viewModel.getSolutionText()
                    true
                }
                else -> false
            }
        }

        binding.boardView.onTileClickedListener = { row: Int, column: Int ->
            if (game == GameState.WAITING) viewModel.pieceTouched(row, column)
            else if (game == GameState.PIECE_TOUCHED) viewModel.movePieceIfPossible(row, column)
        }

    }


}