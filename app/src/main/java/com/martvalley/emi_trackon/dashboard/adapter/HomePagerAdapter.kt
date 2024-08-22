package com.martvalley.emi_trackon.dashboard.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.databinding.ItemHomeViewpagerBannerBinding

class HomePagerAdapter(
    private val images:List<String>
) : RecyclerView.Adapter<HomePagerAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemHomeViewpagerBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String) {
            Glide.with(binding.root).load(image).placeholder(R.drawable.no_image).into(binding.imageViewMain)

            val shimmer = Shimmer.AlphaHighlightBuilder() // or Shimmer.ColorHighlightBuilder()
                .setDuration(2000L) // Duration of the shimmer effect (slow it down)
                .setBaseAlpha(0.9f) // Base alpha (transparency level)
                .setHighlightAlpha(0.7f) // Highlight alpha (transparency level)

                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT) // Shimmer direction
                .build()
            binding.shimmerLayout.apply {
                setShimmer(shimmer)
                startShimmer()

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHomeViewpagerBannerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(images[position])
    }
}