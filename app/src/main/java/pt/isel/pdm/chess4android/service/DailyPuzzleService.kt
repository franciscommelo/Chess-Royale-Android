package pt.isel.pdm.chess4android.service

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.*

private const val URL = "https://lichess.org/api/"

interface DailyPuzzleService {

    companion object {
        fun getInstance(): DailyPuzzleService {
            val retrofit = Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(DailyPuzzleService::class.java)
        }
    }

    @Parcelize
    data class PuzzleDTO(val id : Int, val pgn: String?, var isFinished: Boolean, val date : Date, var solution: List<String>, var isShowSolution : Boolean? = null ) : Parcelable



    @GET("puzzle/daily")
    fun getPuzzle(): Call<PuzzleInfo>

    data class PuzzleInfo(
        @SerializedName("game")
        val game: Game,
        @SerializedName("puzzle")
        val puzzle: Puzzle,
    )

    data class Game(
        @SerializedName("pgn")
        val pgn: String,
    )

    data class Puzzle(
        @SerializedName("solution")
        val solution: List<String>,
    )


    class ServiceUnavailable(message: String = "", cause: Throwable? = null) : Exception(message, cause)

}