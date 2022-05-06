package pt.isel.pdm.chess4android.challenges.list

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.challenges.ChallengeInfo
import pt.isel.pdm.chess4android.challenges.create.CreateChallengeActivity
import pt.isel.pdm.chess4android.challenges.model.Player
import pt.isel.pdm.chess4android.databinding.ActivityChallengesListBinding
import pt.isel.pdm.chess4android.onlineGame.OnlineChessActivity

class ChallengesListActivity : AppCompatActivity() {

    private val binding by lazy { ActivityChallengesListBinding.inflate(layoutInflater) }
    private val viewModel: ChallengesListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.challengesList.setHasFixedSize(true)
        binding.challengesList.layoutManager = LinearLayoutManager(this)

        viewModel.challenges.observe(this) { result ->
            result.onSuccess {
                binding.challengesList.adapter = ChallengesListAdapter(it, ::challengeSelected)
                binding.refreshLayout.isRefreshing = false
            }
            result.onFailure {
                Toast.makeText(this, R.string.error_getting_list, Toast.LENGTH_LONG).show()
            }
        }

        binding.refreshLayout.setOnRefreshListener { updateChallengesList() }
        binding.createChallengeButton.setOnClickListener {
            startActivity(Intent(this, CreateChallengeActivity::class.java))
        }


        viewModel.enrolmentResult.observe(this) {
            it?.onSuccess { createdGameInfo ->
                val intent = OnlineChessActivity.buildIntent(
                    origin = this,
                    turn = Player.firstToMove,
                    local = Player.firstToMove.other,
                    challengeInfo = createdGameInfo.first,
                    lastMove = null,
                    boardFEN = createdGameInfo.second.boardFEN
                )
                startActivity(intent)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        updateChallengesList()
    }


    private fun updateChallengesList() {
        binding.refreshLayout.isRefreshing = true
        viewModel.fetchChallenges()
    }

    private fun challengeSelected(challenge: ChallengeInfo) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.accept_challenge_dialog_title, challenge.challengerName))
            .setPositiveButton(R.string.accept_challenge_dialog_ok) { _, _ ->
                viewModel.tryAcceptChallenge(challenge)
            }
            .setNegativeButton(R.string.accept_challenge_dialog_cancel, null)
            .create()
            .show()
    }
}