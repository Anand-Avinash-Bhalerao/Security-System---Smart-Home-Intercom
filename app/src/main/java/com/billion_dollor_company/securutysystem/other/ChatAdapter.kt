package com.billion_dollor_company.securutysystem.other

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.billion_dollor_company.securutysystem.R
import com.billion_dollor_company.securutysystem.model.MessageInfo
import com.bumptech.glide.Glide


private val IS_DEVICE = 0
private val IS_USER = 1
private val IS_OTHER_USER = 2

class ChatAdapter(
    val context: Context,
    val userUid: String,
    private val list: List<MessageInfo>
) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view:View
        if(viewType == IS_USER){
            view = layoutInflater.inflate(R.layout.chat_send_item, parent, false)
            return SendViewHolder(view)
        }else if(viewType == IS_DEVICE){
            view = layoutInflater.inflate(R.layout.chat_recieved_device_item, parent, false)
            return DeviceViewHolder(view)
        }else {
            view = layoutInflater.inflate(R.layout.chat_recieved_user_item, parent, false)
            return ReceiveViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = list[position]
        if(current.uid == userUid){
            val tempHolder:SendViewHolder = holder as SendViewHolder
            tempHolder.dateS.text = current.time
            tempHolder.messageS.text = current.text
        }else if (!current.isDevice && !current.sentFromDevice){
            val tempHolder:ReceiveViewHolder = holder as ReceiveViewHolder
            tempHolder.dateR.text = current.time
            tempHolder.messageR.text = current.text
            tempHolder.nameR.text = current.name
//              Glide.with(tempHolder.imageR).load(current.dp_bitmap).into(tempHolder.imageR)
        }else{
            val tempHolder:DeviceViewHolder = holder as DeviceViewHolder
            tempHolder.dateD.text = current.time
            tempHolder.messageD.text = current.text
            tempHolder.nameD.text = current.name
              Glide.with(tempHolder.imageD).load(current.dp_bitmap).into(tempHolder.imageD)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {

        return when {
            list[position].uid == userUid -> IS_USER
            list[position].sentFromDevice -> IS_DEVICE
            list[position].isDevice -> IS_DEVICE
            else -> IS_OTHER_USER
        }
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameR: TextView = itemView.findViewById(R.id.receive_name)
        var messageR: TextView = itemView.findViewById(R.id.receive_text)
        var dateR: TextView = itemView.findViewById(R.id.receive_date_time)
    }
    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameD: TextView = itemView.findViewById(R.id.receive_name_device)
        var messageD: TextView = itemView.findViewById(R.id.receive_text_device)
        var dateD: TextView = itemView.findViewById(R.id.receive_date_time_text_device)
        var imageD: ImageView = itemView.findViewById(R.id.receive_image_device)
    }

    class SendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageS: TextView = itemView.findViewById(R.id.send_text)
        var dateS: TextView = itemView.findViewById(R.id.send_date_time)
    }


}
