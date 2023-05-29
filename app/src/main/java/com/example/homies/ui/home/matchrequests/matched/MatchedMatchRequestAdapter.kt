package com.example.homies.ui.home.matchrequests.matched

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homies.R
import com.example.homies.data.model.MatchRequest
import com.example.homies.data.model.Student
import com.example.homies.databinding.ItemMatchedMatchRequestBinding
import com.example.homies.util.openEmail
import com.example.homies.util.openWhatsapp

class MatchedMatchRequestAdapter(
    private val requests: MutableList<MatchRequest.Matched>,
    private val onStudentClick: (Student) -> Unit,
    private val onAgree: (MatchRequest.Matched) -> Unit,
    private val onDisagree: (MatchRequest.Matched) -> Unit,
) : RecyclerView.Adapter<MatchedMatchRequestAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemMatchedMatchRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindTo(request: MatchRequest.Matched) {
            binding.apply {
                textViewName.text = request.targetStudent.fullName
                textViewEducation.isVisible = request.targetStudent.education != null
                textViewEducation.text = request.targetStudent.education?.toString()
                Glide.with(root).load(request.targetStudent.imageUrl)
                    .placeholder(R.drawable.image_placeholder).into(shapeableImageView)

                fabAgree.setOnClickListener {
                    onAgree(request)
                }
                fabDisagree.setOnClickListener {
                    onDisagree(request)
                }
                root.setOnClickListener {
                    onStudentClick(request.targetStudent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMatchedMatchRequestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = requests[position]
        holder.bindTo(request)
    }

    override fun getItemCount() = requests.size

    fun removeItem(request: MatchRequest.Matched) {
        val position = requests.indexOf(request)
        requests.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, requests.size)
    }

}