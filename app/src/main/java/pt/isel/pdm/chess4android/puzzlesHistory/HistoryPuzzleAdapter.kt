package pt.isel.pdm.chess4android.puzzlesHistory

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.service.DailyPuzzleService
import java.text.SimpleDateFormat


class HistoryPuzzleAdapter internal constructor(
    private val dataSource: List<DailyPuzzleService.PuzzleDTO>,
    private val ctx: Context,
    private val onItemCLick: (DailyPuzzleService.PuzzleDTO) -> Unit,
) : RecyclerView.Adapter<HistoryItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.puzzle_item, parent, false)
        return HistoryItemViewHolder(view)
    }

    override fun getItemCount() = dataSource.size

    @SuppressLint("SetTextI18n", "SimpleDateFormat", "ResourceAsColor")
    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {

        val isFinished = dataSource[position].isFinished

        val pattern = "dd-MM-yyyy"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val date: String = simpleDateFormat.format(dataSource[position].date)

        val dateText = holder.dateText
        val turnsText = holder.turnText
        val btn = holder.btn
        val img = holder.img
        val isCompleteText = holder.isCompleteText


        val leftImage: Int
        val backgroudColor: Int
        val isCompleteString: String

        dateText.text = "Puzzle : $date"
        turnsText.text = ctx.getString(R.string.moves_left_history,
            dataSource[position].solution.size.toString())

        if (isFinished) {
            leftImage = R.drawable.ic_puzzle_completed
            backgroudColor = ctx.getColor(R.color.chess_board_black)
            isCompleteString = ctx.getString(R.string.puzzle_solved)


        } else {

            leftImage = R.drawable.ic_puzzle_not_complete
            backgroudColor = ctx.getColor(R.color.chess_board_white)
            isCompleteString = ctx.getString(R.string.puzzle_not_solved)

        }

        img.setImageResource(leftImage)
        btn.setBackgroundColor(backgroudColor)
        isCompleteText.text = isCompleteString

        btn.setOnClickListener {
            onItemCLick.invoke(dataSource[position])
        }
    }


}




