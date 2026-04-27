package com.example.savesmart.ui.categories

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.savesmart.databinding.ItemColorPickerBinding

/**
 * Adapter for selecting a category color (Requirement R05).
 */
class ColorPickerAdapter(
    private val colors: List<String>,
    private var selectedColor: String,
    private val onColorSelected: (String) -> Unit
) : RecyclerView.Adapter<ColorPickerAdapter.ColorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding = ItemColorPickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colors[position])
    }

    override fun getItemCount(): Int = colors.size

    inner class ColorViewHolder(private val binding: ItemColorPickerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(colorHex: String) {
            binding.viewColor.setBackgroundColor(Color.parseColor(colorHex))
            binding.ivSelected.visibility = if (colorHex == selectedColor) View.VISIBLE else View.GONE
            
            binding.root.setOnClickListener {
                val oldSelected = selectedColor
                selectedColor = colorHex
                notifyItemChanged(colors.indexOf(oldSelected))
                notifyItemChanged(colors.indexOf(selectedColor))
                onColorSelected(colorHex)
            }
        }
    }
    
    fun setSelectedColor(color: String) {
        val oldIndex = colors.indexOf(selectedColor)
        selectedColor = color
        val newIndex = colors.indexOf(selectedColor)
        if (oldIndex != -1) notifyItemChanged(oldIndex)
        if (newIndex != -1) notifyItemChanged(newIndex)
    }
}
