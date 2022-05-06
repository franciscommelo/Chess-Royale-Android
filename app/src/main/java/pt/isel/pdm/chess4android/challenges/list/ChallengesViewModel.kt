package pt.isel.pdm.chess4android.challenges.list

import android.app.Application
import android.view.Gravity
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pt.isel.pdm.chess4android.ChessApplication
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.challenges.ChallengeInfo
import pt.isel.pdm.chess4android.repo.ChessGameState

class ChallengesListViewModel(app: Application) : AndroidViewModel(app) {

    private val app = getApplication<ChessApplication>()


    private val _challenges: MutableLiveData<Result<List<ChallengeInfo>>> = MutableLiveData()
    val challenges: LiveData<Result<List<ChallengeInfo>>> = _challenges

    fun fetchChallenges() =
        app.challengesRepository.fetchChallenges(onComplete = {
            _challenges.value = it

            _challenges.value?.onSuccess { it ->
                if (it.isEmpty()) {
                    val toast = Toast.makeText(this.app, R.string.no_challenges_available, Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }
            }
        })

    private val _enrolmentResult: MutableLiveData<Result<Pair<ChallengeInfo, ChessGameState>>?> =
        MutableLiveData()
    val enrolmentResult: LiveData<Result<Pair<ChallengeInfo, ChessGameState>>?> = _enrolmentResult

    fun tryAcceptChallenge(challengeInfo: ChallengeInfo) {
        val app = getApplication<ChessApplication>()
        app.challengesRepository.withdrawChallenge(
            challengeId = challengeInfo.id,
            onComplete = {
                it.onSuccess {
                    app.gameRepository.createGame(challengeInfo, onComplete = { game ->
                        _enrolmentResult.value = game
                    })
                }
            }
        )
    }
}
