package pt.isel.pdm.chess4android

import android.app.Application
import androidx.room.Room
import androidx.work.*
import com.google.gson.Gson
import pt.isel.pdm.chess4android.challenges.ChallengesRepository
import pt.isel.pdm.chess4android.database.Database
import pt.isel.pdm.chess4android.puzzle.DownloadDailyPuzzleWorker
import pt.isel.pdm.chess4android.repo.GamesRepository
import pt.isel.pdm.chess4android.repo.PuzzlesRepository
import pt.isel.pdm.chess4android.service.DailyPuzzleService
import java.util.concurrent.TimeUnit

class ChessApplication : Application() {

    private val mapper: Gson by lazy { Gson() }


    val puzzleRepository: PuzzlesRepository by lazy {
        PuzzlesRepository(
            Room
                .databaseBuilder(this, Database::class.java, "history_db")
                .fallbackToDestructiveMigration()
                .build(),
            DailyPuzzleService.getInstance()
        )
    }


    val challengesRepository: ChallengesRepository by lazy { ChallengesRepository() }


    val gameRepository: GamesRepository by lazy { GamesRepository(mapper) }


    override fun onCreate() {

        super.onCreate()
        val workRequest = PeriodicWorkRequestBuilder<DownloadDailyPuzzleWorker>(1, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .build()

        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                "DownloadDailyPuzzle",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )

    }


}