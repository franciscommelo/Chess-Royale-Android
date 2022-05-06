package pt.isel.pdm.chess4android.database.daos

import androidx.room.*
import pt.isel.pdm.chess4android.database.entities.Puzzle

import java.util.*

@Dao
interface PuzzleDao {

    @Query("select * from puzzles where puzzleId = :id")
    suspend fun getPuzzleById(id: Int): Puzzle

    @Query("select * from (select * from puzzles limit 100) ORDER BY timestamp DESC;\n")
    fun getAllPuzzles(): List<Puzzle>

    @Insert
    fun createPuzzle(puzzle: Puzzle)

    @Query("select  EXISTS( select * from  puzzles where timestamp = :date)")
    fun isDateExist(date : Date): Boolean

    @Update
    suspend fun setPuzzleState(puzzle : Puzzle) : Int

    @Delete
    suspend fun removePuzzle(puzzle: Puzzle) : Int

    @Query("SELECT * FROM puzzles ORDER BY puzzleId DESC LIMIT :count")
    fun getLast(count: Int): List<Puzzle>

}