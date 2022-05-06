package pt.isel.pdm.chess4android.challenges.create


import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.challenges.model.Player
import pt.isel.pdm.chess4android.databinding.ActivityCreateChallengeBinding
import pt.isel.pdm.chess4android.model.Board
import pt.isel.pdm.chess4android.onlineGame.OnlineChessActivity


class
CreateChallengeActivity : AppCompatActivity() {

    private val viewModel: CreateChallengeViewModel by viewModels()
    private val binding: ActivityCreateChallengeBinding by lazy {
        ActivityCreateChallengeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.created.observe(this) {
            if (it == null) displayCreateChallenge()
            else it.onFailure { displayError() }.onSuccess {
                displayWaitingForChallenger()
            }
        }

        viewModel.accepted.observe(this) {
            if (it == true) {
                viewModel.created.value?.onSuccess { challenge ->
                    val intent = OnlineChessActivity.buildIntent(
                        origin = this,
                        turn = Player.firstToMove,
                        local = Player.firstToMove,
                        boardFEN = Board.STARTING_POSITION_FEN,
                        challengeInfo = challenge,
                        lastMove = null
                    )
                    startActivity(intent)
                }
            }
        }

        binding.action.setOnClickListener {

            if(binding.name.text.toString().isNotBlank()) {
                if (viewModel.created.value == null)
                    viewModel.createChallenge(
                        binding.name.text.toString(),
                        binding.message.text.toString()
                    )
                else viewModel.removeChallenge()
            }
            else Toast.makeText(this, resources.getString(R.string.name_field_warning), Toast.LENGTH_SHORT).show()
        }
    }


    private fun displayCreateChallenge() {
        binding.action.text = getString(R.string.create_challenge_button_label)
        with(binding.name) { text.clear(); isEnabled = true }
        with(binding.message) { text.clear(); isEnabled = true }
        binding.loading.isVisible = false
        binding.waitingMessage.isVisible = false
    }

    private fun displayWaitingForChallenger() {
        binding.action.text = getString(R.string.cancel_challenge_button_label)
        binding.name.isEnabled = false
        binding.message.isEnabled = false
        binding.loading.isVisible = true
        binding.waitingMessage.isVisible = true
    }

    private fun displayError() {
        displayCreateChallenge()
        Toast
            .makeText(this, R.string.return_button, Toast.LENGTH_LONG)
            .show()
    }
}