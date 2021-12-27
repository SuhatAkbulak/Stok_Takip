package com.maltepeuniversiyesi.stok.ui

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.maltepeuniversiyesi.stok.databinding.FragmentTaskBinding
import com.maltepeuniversiyesi.stok.models.TaskModel

import com.maltepeuniversiyesi.stok.ui.placeholder.PlaceholderContent.PlaceholderItem


/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class TaskRecyclerViewAdapter(
    private val values: List<TaskModel>, val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val index = position + 1;

        holder.name.text = item.urun_isim


        holder.textStockCount.text = item.stok_sayi.toString()
        holder.textBarcode.text = item.urun_barkod
        holder.bind(item, itemClickListener)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentTaskBinding) : RecyclerView.ViewHolder(binding.root) {

        val textStockCount: TextView = binding.textStockCount
        val textBarcode: TextView = binding.textBarcode
        val name: TextView = binding.textName
        private val btnChangeStock: Button = binding.btnChangeStock


        override fun toString(): String {
            return super.toString() + " '" + name.text + "'"
        }

        fun bind(item: TaskModel, itemClickListener: OnItemClickListener) {
            btnChangeStock.setOnClickListener { itemClickListener.onStockChangeRequested(item) }

        }
    }

    interface OnItemClickListener {
        fun onStockChangeRequested(item: TaskModel?)

    }

}