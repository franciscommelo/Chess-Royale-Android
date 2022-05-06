package pt.isel.pdm.chess4android.database


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pt.isel.pdm.chess4android.database.daos.PuzzleDao
import pt.isel.pdm.chess4android.database.entities.Converters
import pt.isel.pdm.chess4android.database.entities.Puzzle

@Database(entities = [Puzzle::class], version = 4)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun getPuzzleDao(): PuzzleDao
}


