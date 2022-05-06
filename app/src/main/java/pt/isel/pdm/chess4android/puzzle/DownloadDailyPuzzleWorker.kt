package pt.isel.pdm.chess4android.puzzle

import android.content.Context
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import pt.isel.pdm.chess4android.ChessApplication

class DownloadDailyPuzzleWorker(appContext: Context, workerParams: WorkerParameters)
    : ListenableWorker(appContext, workerParams) {

    override fun startWork(): ListenableFuture<Result> {
        val app : ChessApplication = applicationContext as ChessApplication
        val repo = app.puzzleRepository


        return CallbackToFutureAdapter.getFuture { completer ->
            repo.fetchPuzzleOfDay(mustSaveToDB = true) { result ->
                result
                    .onSuccess {
                        completer.set(Result.success())
                    }
                    .onFailure {
                        completer.setException(it)
                    }
            }
        }
    }
}