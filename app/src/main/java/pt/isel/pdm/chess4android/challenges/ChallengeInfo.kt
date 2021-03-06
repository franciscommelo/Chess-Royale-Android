package pt.isel.pdm.chess4android.challenges

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChallengeInfo(
    val id: String,
    val challengerName: String,
    val challengerMessage: String
) : Parcelable