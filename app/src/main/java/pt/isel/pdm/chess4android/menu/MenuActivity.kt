package pt.isel.pdm.chess4android.menu

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.challenges.list.ChallengesListActivity
import pt.isel.pdm.chess4android.databinding.LandingPageBinding
import pt.isel.pdm.chess4android.offlineGame.GameActivity
import pt.isel.pdm.chess4android.puzzlesHistory.HistoryActivity

class MenuActivity : AppCompatActivity() {


    private val binding by lazy {
        LandingPageBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val fading : Animation = AnimationUtils.loadAnimation(this, R.anim.fade)
        fading.duration = 1500
        binding.logo.startAnimation(fading)


        binding.playPuzzle.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }


        binding.aboutUs.setOnClickListener {
            val intent = Intent(this, AboutUsActivity::class.java)
            startActivity(intent)
        }


        binding.playGameOffline?.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        binding.playGameOnline?.setOnClickListener {
            val intent = Intent(this, ChallengesListActivity::class.java)
            startActivity(intent)
        }


    }
}