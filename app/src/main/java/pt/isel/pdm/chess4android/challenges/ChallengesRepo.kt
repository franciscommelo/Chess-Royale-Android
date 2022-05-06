package pt.isel.pdm.chess4android.challenges

import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
private const val CHALLENGES_COLLECTION = "challenges"
private const val CHALLENGER_NAME = "challengerName"
private const val CHALLENGER_MESSAGE = "challengerMessage"


class ChallengesRepository {

    fun fetchChallenges(onComplete: (Result<List<ChallengeInfo>>?) -> Unit) {
        val limit = 30
        Firebase.firestore.collection(CHALLENGES_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                onComplete(Result.success(result.take(limit).map { it.toChallengeInfo() }))
            }
            .addOnFailureListener {
                onComplete(Result.failure(it))
            }
    }


    fun publishChallenge(
        name: String,
        message: String,
        onComplete: (Result<ChallengeInfo>) -> Unit
    ) {
        Firebase.firestore.collection(CHALLENGES_COLLECTION)
            .add(hashMapOf(CHALLENGER_NAME to name, CHALLENGER_MESSAGE to message))
            .addOnSuccessListener {
                onComplete(Result.success(ChallengeInfo(it.id, name, message)))
            }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }



    fun withdrawChallenge(challengeId: String, onComplete: (Result<Unit>) -> Unit) {
        Firebase.firestore
            .collection(CHALLENGES_COLLECTION)
            .document(challengeId)
            .delete()
            .addOnSuccessListener { onComplete(Result.success(Unit)) }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }

    fun subscribeToChallengeAcceptance(
        challengeId: String,
        onSubscriptionError: (Exception) -> Unit,
        onChallengeAccepted: () -> Unit
    ): ListenerRegistration {

        return Firebase.firestore
            .collection(CHALLENGES_COLLECTION)
            .document(challengeId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onSubscriptionError(error)
                    return@addSnapshotListener
                }

                if (snapshot?.exists() == false) {
                    onChallengeAccepted()
                }
            }
    }


    fun unsubscribeToChallengeAcceptance(subscription: ListenerRegistration) {
        subscription.remove()
    }
}

private fun QueryDocumentSnapshot.toChallengeInfo() =
    ChallengeInfo(
        id,
        data[CHALLENGER_NAME] as String,
        data[CHALLENGER_MESSAGE] as String
    )
