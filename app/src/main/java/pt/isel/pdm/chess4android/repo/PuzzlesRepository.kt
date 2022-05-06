package pt.isel.pdm.chess4android.repo

import pt.isel.pdm.chess4android.database.Database
import pt.isel.pdm.chess4android.database.entities.Puzzle
import pt.isel.pdm.chess4android.service.DailyPuzzleService
import pt.isel.pdm.chess4android.service.DailyPuzzleService.*
import pt.isel.pdm.chess4android.utils.callbackAfterAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*


class PuzzlesRepository(private val db: Database, private val service: DailyPuzzleService) {


    fun getAllPuzzles(): List<Puzzle> = db.getPuzzleDao().getAllPuzzles()


    private fun asyncMaybeGetTodayPuzzleFromDB(callback: (Result<Puzzle?>) -> Unit) {
        callbackAfterAsync(callback) {
            db.getPuzzleDao().getLast(1).firstOrNull()
        }
    }


    suspend fun setPuzzleHasFinished(puzzle: PuzzleDTO) {
        val dao = db.getPuzzleDao()
        try {
            dao.setPuzzleState(Puzzle(puzzleId = puzzle.id,
                isFinished = true,
                timestamp = puzzle.date,
                pgn = puzzle.pgn!!,
                solution = puzzle.solution))
        } catch (message: Exception) {
            println("Exception Message -> $message")
        }
    }


    private fun asyncGetTodayPuzzleFromAPI(callback: (Result<PuzzleInfo>) -> Unit) {
        service.getPuzzle().enqueue(
            object: Callback<PuzzleInfo> {
                override fun onResponse(call: Call<PuzzleInfo>, response: Response<PuzzleInfo>) {

                    val dailyQuote: PuzzleInfo? = response.body()
                    val result =
                        if (dailyQuote != null && response.isSuccessful)
                            Result.success(dailyQuote)
                        else
                            Result.failure(ServiceUnavailable())
                    callback(result)
                }

                override fun onFailure(call: Call<PuzzleInfo>, error: Throwable) {

                    callback(Result.failure(ServiceUnavailable(cause = error)))
                }
            })
    }


    fun fetchPuzzleOfDay(mustSaveToDB: Boolean = false, callback: ((Result<PuzzleInfo>) -> Unit)?) {
        asyncMaybeGetTodayPuzzleFromDB {
            asyncGetTodayPuzzleFromAPI { apiResult ->
                apiResult.onSuccess { quoteDto ->
                    asyncSaveToDB(quoteDto) { saveToDBResult ->
                        saveToDBResult.onSuccess {
                            callback?.invoke(Result.success(quoteDto))
                        }
                            .onFailure {
                                callback?.let { it1 -> it1(if(mustSaveToDB) Result.failure(it) else Result.success(quoteDto)) }
                            }
                    }
                }
                callback?.invoke(apiResult)
            }
        }
    }


    private fun asyncSaveToDB(dto: PuzzleInfo, callback: (Result<Unit>) -> Unit = { }) {
        callbackAfterAsync(callback) {
            val timestamp: Date = Date.from(Instant.now().truncatedTo(ChronoUnit.DAYS))
            db.getPuzzleDao().createPuzzle(
                Puzzle(
                    pgn = dto.game.pgn,
                    isFinished = false,
                    timestamp = timestamp,
                    solution = dto.puzzle.solution
                )
            )
        }
    }
}




