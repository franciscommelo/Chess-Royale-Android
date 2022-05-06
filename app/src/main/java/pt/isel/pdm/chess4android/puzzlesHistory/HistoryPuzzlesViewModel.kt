package pt.isel.pdm.chess4android.puzzlesHistory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pt.isel.pdm.chess4android.ChessApplication
import pt.isel.pdm.chess4android.repo.PuzzlesRepository
import pt.isel.pdm.chess4android.service.DailyPuzzleService.PuzzleDTO
import pt.isel.pdm.chess4android.utils.callbackAfterAsync

class HistoryPuzzlesViewModel(application: Application) : AndroidViewModel(application) {

    private val app: ChessApplication by lazy { getApplication() }
    private val puzzleRepository: PuzzlesRepository by lazy { app.puzzleRepository }
    var puzzleHistory: LiveData<List<PuzzleDTO>>? = null


    fun loadHistory(): LiveData<List<PuzzleDTO>> {
        val publish = MutableLiveData<List<PuzzleDTO>>()
        puzzleHistory = publish

        callbackAfterAsync(
            asyncAction = {
                if(puzzleRepository.getAllPuzzles().isEmpty()){
                    puzzleRepository.fetchPuzzleOfDay(false, null)
                }
            },
            callback = { result ->
                result.onSuccess {}

            }
        )

        callbackAfterAsync(
            asyncAction = {
                puzzleRepository.getAllPuzzles().map {
                    PuzzleDTO(
                        id = it.puzzleId,
                        pgn = it.pgn,
                        isFinished = it.isFinished,
                        date = it.timestamp,
                        solution = it.solution
                    )
                }
            },
            callback = { result ->
                result.onSuccess {
                    publish.value = it
                }
                result.onFailure { publish.value = emptyList() }
            }
        )
        return publish
    }
}