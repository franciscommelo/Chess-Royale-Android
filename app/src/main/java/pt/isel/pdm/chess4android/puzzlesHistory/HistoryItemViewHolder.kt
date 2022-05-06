package pt.isel.pdm.chess4android.puzzlesHistory

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.isel.pdm.chess4android.R

class HistoryItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val btn: View = itemView.findViewById(R.id.puzzleName)
    val img: ImageView = itemView.findViewById(R.id.image)
    val dateText : TextView = itemView.findViewById(R.id.puzzle_date_text)
    val turnText : TextView = itemView.findViewById(R.id.turns_text)
    val isCompleteText : TextView = itemView.findViewById(R.id.isComplete)
}
