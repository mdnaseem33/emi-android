package com.martvalley.emi_trackon.dashboard.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.dashboard.home.Banner
import com.martvalley.emi_trackon.databinding.ItemHomeViewpagerBannerBinding
import com.martvalley.emi_trackon.utils.Constants
import com.martvalley.emi_trackon.utils.showToast

class HomePagerAdapter(
    private val images:List<Banner>, val context: Context
) : RecyclerView.Adapter<HomePagerAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemHomeViewpagerBannerBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Banner) {
            Glide.with(binding.root).load(Constants.BASEURL+image.image).placeholder(R.drawable.no_image).into(binding.imageViewMain)
            binding.root.setOnClickListener {
                if (image.link != null){
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(image.link.toString())
                    }
                    context.startActivity(intent)
                }else{
                    context.showToast( "No link found")
                }

            }
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
            ), context
        )
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(images[position])
    }
}