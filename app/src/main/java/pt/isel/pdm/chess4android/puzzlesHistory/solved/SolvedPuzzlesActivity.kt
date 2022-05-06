package pt.isel.pdm.chess4android.puzzlesHistory.solved

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chess4android.databinding.SolvedPuzzlesBinding
import pt.isel.pdm.chess4android.puzzle.MainActivity
import pt.isel.pdm.chess4android.service.DailyPuzzleService


private const val Puzzle_Solution_EXTRA = "Puzzle_Solution.Extra.puzzle"

class SolvedPuzzlesActivity : AppCompatActivity() {

    companion object {
        fun buildIntent(origin: Activity, puzzleDTO: DailyPuzzleService.PuzzleDTO): Intent {
            val msg = Intent(origin, SolvedPuzzlesActivity::class.java)
            msg.putExtra(Puzzle_Solution_EXTRA, puzzleDTO)
            return msg
        }
    }




    private val binding by lazy {
        SolvedPuzzlesBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val puzzle = intent.getParcelableExtra<DailyPuzzleService.PuzzleDTO>(Puzzle_Solution_EXTRA)


        binding.showSolutionBtn.setOnClickListener {
            puzzle?.isShowSolution = true
           startActivity( MainActivity.buildIntent(this, puzzle!! ))
        }

        binding.solvePuzzle.setOnClickListener {
            puzzle?.isShowSolution = false
            startActivity(MainActivity.buildIntent(this, puzzle!! ))
        }


    }




}