package com.branchx.agent.helper.extns

import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.branchx.agent.R
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.branchx.agent.helper.util.AppUtil

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.alphaFull() {
    this.alpha = 1f
}

fun View.alphaHalf() {
    this.alpha = 0.5f
}


fun viewEnable(vararg views: View, value: Boolean) {
    views.forEach {
        if (value) {
            it.alphaFull()
            it.isEnabled = true
        } else {
            it.alphaHalf()
            it.isEnabled = false
        }
    }
}

fun TextView.setupTextColor(color: Int) {
    this.setTextColor(ContextCompat.getColor(this.context, color))
}

fun View.setupBGColor(color : Int){
    this.setBackgroundColor(ContextCompat.getColor(this.context, color))
}


fun LottieAnimationView.set(type: LottieType) {
    val fileName = when (type) {
        LottieType.ALERT -> "lottie_alert.json"
        LottieType.SUCCESS -> "lottie_success.json"
        LottieType.FAILED -> "lottie_failed.json"
        LottieType.PENDING -> "pending_new.json"
        LottieType.WARNING -> "lottie_warning.json"
        LottieType.TIME_OUT -> "lottie_time_out.json"
        LottieType.NO_INTERNET -> "lottie_no_internet.json"
        LottieType.SERVER -> "lottie_server.json"
    }

    this.setAnimation(fileName)
    this.playAnimation()
}

enum class LottieType {
    SUCCESS, FAILED, PENDING, ALERT, WARNING, NO_INTERNET, SERVER, TIME_OUT
}

//SETTING UP IMAGE INTO GLIDE

fun ImageView.setGlideImage(imgUrl: String) {

    val imageView: ImageView = this


    Glide.with(this.context)
        .load(imgUrl)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        imageView.context,
                        R.drawable.no_photo
                    )
                )
                return true
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                imageView.setImageDrawable(resource)
                return true
            }

        }).submit()
}


fun RecyclerView.setup(): RecyclerView {
    this.setHasFixedSize(false)
    this.layoutManager = LinearLayoutManager(this.context)
    this.adapter = adapter
    return this
}


//EditTextWatcher
fun EditText.afterTextChanged(onChanged: (s: CharSequence?) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onChanged(s)
        }
    })
}

fun View.isUserInteractionEnabled(enabled: Boolean) {
    isEnabled = enabled
    if (this is ViewGroup && this.childCount > 0) {
        this.children.forEach {
            it.isUserInteractionEnabled(enabled)
        }
    }
}

fun Spinner.setupAdapter(list: Array<String>,
                         onItemSelected: (String) -> Unit
) {
    val dataAdapter = ArrayAdapter(context,
        R.layout.spinner_text_view, list)
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    this.adapter = dataAdapter
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
            val value = this@setupAdapter.selectedItem.toString()
            onItemSelected(value)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}

inline fun EditText.onTextChange(crossinline onChange : (String)->Unit){

    this.addTextChangedListener(object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onChange(this@onTextChange.text.toString())
        }

        override fun afterTextChanged(s: Editable?) {

        }

    })
}

fun Button.disable(value : Boolean = true){

    if(value){
        this.isEnabled = false
        this.isClickable = false
    }else {
        this.isEnabled = true
        this.isClickable = true
    }

}
fun ImageView.setupNetworkImage(url : String) {
    AppUtil.logger("networkImage : $url")
    Glide.with(this.context)
        .load(url)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {

                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {

                return false
            }
        })
        .into(this)
}