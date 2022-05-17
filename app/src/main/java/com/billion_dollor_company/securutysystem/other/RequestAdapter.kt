package com.billion_dollor_company.securutysystem.other

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.billion_dollor_company.securutysystem.R
import com.billion_dollor_company.securutysystem.model.RequestInfo
import de.hdodenhof.circleimageview.CircleImageView

class RequestAdapter(
    val context: Context,
    val list: List<RequestInfo>,
    val onNoteClickListener: OnNoteClickListener
) : RecyclerView.Adapter<RequestAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.accept_req_recycler_item, parent, false)
        return ViewHolder(view, onNoteClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val requestInfo: RequestInfo = list[position]
        val name: TextView = holder.name
        val date: TextView = holder.date
        val photo: CircleImageView = holder.photo

        val accept: ConstraintLayout = holder.accept
        val decline: ImageView = holder.decline
        name.text = requestInfo.Name
        date.text = requestInfo.Date


//        photo.setCircleBackgroundColorResource(requestInfo.photo)

        accept.setOnClickListener {
            onNoteClickListener.onAccept(position)
        }
        decline.setOnClickListener {
            onNoteClickListener.onDecline(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemView: View, onNoteClickListener: OnNoteClickListener) :
        RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var date: TextView
        var photo: CircleImageView
        var accept: ConstraintLayout
        var decline: ImageView


        var onNoteClickListener: OnNoteClickListener


        init {
            this.onNoteClickListener = onNoteClickListener
            name = itemView.findViewById(R.id.personName)
            date = itemView.findViewById(R.id.date)
            photo = itemView.findViewById(R.id.profile_image_item)
            accept = itemView.findViewById(R.id.accept)
            decline = itemView.findViewById(R.id.decline)
        }
    }

    interface OnNoteClickListener {
        fun onAccept(pos: Int)
        fun onDecline(pos: Int)
    }
}