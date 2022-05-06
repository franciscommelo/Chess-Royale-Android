package pt.isel.pdm.chess4android.puzzlesHistory

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pt.isel.pdm.chess4android.databinding.PuzzlesMenuBinding
import pt.isel.pdm.chess4android.puzzle.MainActivity
import pt.isel.pdm.chess4android.puzzlesHistory.solved.SolvedPuzzlesActivity

class HistoryActivity : AppCompatActivity() {

    private val views by lazy { PuzzlesMenuBinding.inflate(layoutInflater) }
    private val viewModel: HistoryPuzzlesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)

        views.puzzleList.layoutManager = LinearLayoutManager(this)

        (viewModel.puzzleHistory ?: viewModel.loadHistory()).observe(this) {
            views.puzzleList.adapter = HistoryPuzzleAdapter(it, this) { puzzleDTO ->
                run {
                    if (!puzzleDTO.isFinished)
                        startActivity(MainActivity.buildIntent(this, puzzleDTO))
                    else {
                        startActivity(SolvedPuzzlesActivity.buildIntent(this, puzzleDTO))
                    }
                }
            }
        }
    }
}

