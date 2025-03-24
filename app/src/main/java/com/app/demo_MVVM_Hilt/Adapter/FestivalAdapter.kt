package com.app.demo_MVVM_Hilt.Adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.demo_MVVM_Hilt.databinding.ItemFestivalBinding
import com.app.demo_MVVM_Hilt.model.Festival
import com.bumptech.glide.Glide


class FestivalAdapter(var festivals: List<Festival>) : RecyclerView.Adapter<FestivalAdapter.FestivalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FestivalViewHolder {
        val binding = ItemFestivalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FestivalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FestivalViewHolder, position: Int) {
        val festival = festivals[position]
        holder.bind(festival)
    }

    override fun getItemCount(): Int = festivals.size

    inner class FestivalViewHolder(private val binding: ItemFestivalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(festival: Festival) {
            binding.festivalName.text = festival.festival_name
            binding.festivalDate.text = festival.festival_date
            Glide.with(binding.festivalImage.context).load(festival.festival_image).into(binding.festivalImage)
        }
    }
}
