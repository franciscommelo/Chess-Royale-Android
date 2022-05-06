package pt.isel.pdm.chess4android.menu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chess4android.databinding.AboutUsPageBinding

class AboutUsActivity: AppCompatActivity() {


    private val binding by lazy {
        AboutUsPageBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}