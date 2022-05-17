package com.billion_dollor_company.securutysystem.other

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.billion_dollor_company.securutysystem.R

class LoadingDialogBar(private var context: Context) {

    private lateinit var dialog:AlertDialog
    private lateinit var titleTextView: TextView
    private lateinit var lottieAnimation: LottieAnimationView
    private lateinit var lottieAnimationStatus: LottieAnimationView

    fun showDialog(title:String,animation:Int){
        val view:View = LayoutInflater.from(context).inflate(R.layout.dialog_layout,null)
        dialog = AlertDialog.Builder(context).create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setView(view)
        titleTextView = view.findViewById(R.id.dialog_textview)
        titleTextView.text = title

        lottieAnimation = view.findViewById(R.id.dialog_lottieView)
        lottieAnimation.setAnimation(animation)
        dialog.show()

        lottieAnimationStatus = view.findViewById(R.id.dialog_lottieView_status)
    }

    fun setCheck(){

        lottieAnimation.visibility = View.GONE
        titleTextView.visibility = View.GONE

        lottieAnimationStatus.setAnimation(R.raw.loading_correct)
        lottieAnimationStatus.addAnimatorListener(object: AnimatorListener{
            override fun onAnimationStart(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                dismissDialog()
            }

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationRepeat(p0: Animator?) {}

        })
    }

    fun setError(){
        lottieAnimation.visibility = View.GONE
        titleTextView.visibility = View.GONE
        lottieAnimationStatus.setAnimation(R.raw.loading_error)
        lottieAnimationStatus.addAnimatorListener(object: AnimatorListener{
            override fun onAnimationStart(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                dismissDialog()
            }

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationRepeat(p0: Animator?) {}

        })
    }
    fun dismissDialog(){
        dialog.dismiss()
    }

}