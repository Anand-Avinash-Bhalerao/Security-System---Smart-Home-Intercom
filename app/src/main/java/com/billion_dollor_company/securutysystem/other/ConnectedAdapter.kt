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
import com.billion_dollor_company.securutysystem.model.ConnectedDeviceInfo

class ConnectedAdapter(
    val context: Context,
    private val list: List<ConnectedDeviceInfo>,
    private val onNoteClickListener: OnNoteClickListener,
    private val deleteVisibility:Int = View.VISIBLE
) : RecyclerView.Adapter<ConnectedAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.connected_device_item, parent, false)
        return ViewHolder(view,onNoteClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val requestInfo: ConnectedDeviceInfo = list[position]
        val name: TextView = holder.name
        val delete:ImageView = holder.delete
        val container:ConstraintLayout = holder.container
        delete.visibility = deleteVisibility
        name.text = requestInfo.Name

        delete.setOnClickListener{
            onNoteClickListener.onDelete(position)
        }
        container.setOnClickListener{
            onNoteClickListener.onNoteClick(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemView: View,onNoteClickListener: OnNoteClickListener) :
        RecyclerView.ViewHolder(itemView){
        var name: TextView = itemView.findViewById(R.id.name)
        var delete: ImageView = itemView.findViewById(R.id.delete)
        var clickListener = onNoteClickListener
        var container:ConstraintLayout = itemView.findViewById(R.id.container)


    }

    interface OnNoteClickListener {
        fun onDelete(pos:Int)
        fun onNoteClick(pos:Int)
    }
}