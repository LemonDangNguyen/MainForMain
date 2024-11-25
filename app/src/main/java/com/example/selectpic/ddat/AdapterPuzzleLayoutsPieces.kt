package com.hypersoft.puzzlelayouts.app.features.layouts.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.selectpic.databinding.ItemPuzzleLayoutsPieceBinding
import com.hypersoft.pzlayout.interfaces.PuzzleLayout
import com.hypersoft.pzlayout.layouts.slant.NumberSlantLayout
import com.hypersoft.pzlayout.layouts.straight.NumberStraightLayout
class AdapterPuzzleLayoutsPieces(private val itemClick: (puzzleLayout: PuzzleLayout, theme: Int) -> Unit) : RecyclerView.Adapter<AdapterPuzzleLayoutsPieces.CustomViewHolder>() {

    private var puzzleLayouts: List<PuzzleLayout> = emptyList()

    fun setPuzzleLayouts(newPuzzleLayouts: List<PuzzleLayout>) {
        this.puzzleLayouts = newPuzzleLayouts
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPuzzleLayoutsPieceBinding.inflate(layoutInflater, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentItem = puzzleLayouts[position]
        bindViews(holder, currentItem)

        holder.binding.puzzle.apply {
            setLineColor(Color.BLACK)
            setPiecePadding(4.0F)
        }
        holder.binding.root.setOnClickListener {
            var theme = 0
            if (currentItem is NumberSlantLayout) {
                theme = currentItem.theme
            } else if (currentItem is NumberStraightLayout) {
                theme = currentItem.theme
            }
            itemClick.invoke(currentItem, theme)
        }

    }
// tra sữalujuc vị

    override fun getItemCount(): Int {
        return puzzleLayouts.size
    }

    private fun bindViews(holder: CustomViewHolder, currentItem: PuzzleLayout) {
        holder.binding.puzzle.needDrawLine = true
        holder.binding.puzzle.needDrawOuterLine = true
        holder.binding.puzzle.isTouchEnable = false

        holder.binding.puzzle.setPuzzleLayout(currentItem)
    }

    inner class CustomViewHolder(val binding: ItemPuzzleLayoutsPieceBinding) : RecyclerView.ViewHolder(binding.root)
}
