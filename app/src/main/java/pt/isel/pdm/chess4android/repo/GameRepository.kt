package pt.isel.pdm.chess4android.repo


import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import pt.isel.pdm.chess4android.challenges.ChallengeInfo
import pt.isel.pdm.chess4android.challenges.model.Player
import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.Board

private const val GAMES_COLLECTION = "games"
private const val GAME_STATE_KEY = "game"

@Parcelize
data class ChessGameState(
    val id : String,
    val turn : String?,
    val boardFEN : String,
    val lastMove : String?,
    val isCheck : Army?,
    val isCheckMate : Army?,
    val isWithdrawn : Boolean
) : Parcelable


class GamesRepository(private val mapper: Gson) {
    fun createGame(
        challenge: ChallengeInfo,
        onComplete: (Result<Pair<ChallengeInfo, ChessGameState>>?) -> Unit
    ) {

        val gameState = ChessGameState(id = challenge.id, turn = Player.firstToMove.name, Board.STARTING_POSITION_FEN, lastMove = null, isCheck = null, isCheckMate = null, isWithdrawn = false)

        Firebase.firestore.collection(GAMES_COLLECTION)
            .document(challenge.id)
            .set(hashMapOf(GAME_STATE_KEY to mapper.toJson(gameState)))
            .addOnSuccessListener { onComplete(Result.success(Pair(challenge, gameState)))}
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }


    fun subscribeToGameStateChanges(
        challengeId: String,
        onSubscriptionError: (Exception) -> Unit,
        onGameStateChange: (ChessGameState) -> Unit
    ): ListenerRegistration {
        return Firebase.firestore
            .collection(GAMES_COLLECTION)
            .document(challengeId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onSubscriptionError(error)
                    return@addSnapshotListener
                }

                if (snapshot?.exists() == true) {
                    Log.v("SNAP", "SNAPSHOT OCCURRED")
                    val gameState = mapper.fromJson(
                        snapshot.get(GAME_STATE_KEY) as String,
                        ChessGameState::class.java
                    )
                    onGameStateChange(gameState)
                }
            }
    }


    fun deleteGame(challengeId: String, onComplete: (Result<Unit>) -> Unit) {
        Firebase.firestore.collection(GAMES_COLLECTION)
            .document(challengeId)
            .delete()
            .addOnSuccessListener { onComplete(Result.success(Unit)) }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }


    fun updateGameState(chessGameState: ChessGameState, onComplete: (Result<ChessGameState>) -> Unit) {
        Firebase.firestore.collection(GAMES_COLLECTION)
            .document(chessGameState.id)
            .set(hashMapOf(GAME_STATE_KEY to mapper.toJson(chessGameState)))
            .addOnSuccessListener { onComplete(Result.success(chessGameState)) }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }

}