package pt.isel.pdm.chess4android.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.*

@Entity(tableName = "puzzles")
data class Puzzle(
    @PrimaryKey(autoGenerate = true) val puzzleId: Int = 0,
    val pgn: String,
    val isFinished: Boolean,
    val timestamp: Date,
    val solution : List<String>,
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long) = Date(value)

    @TypeConverter
    fun dateToTimestamp(date: Date) = date.time

    @TypeConverter
    fun  fromArray(strings : List<String> ) : String{
        return strings.joinToString (separator = ",")
    }

    @TypeConverter
    fun fromStringToList (string : String) : List<String>{
        return string.split(",").map { it.trim() }
    }


}