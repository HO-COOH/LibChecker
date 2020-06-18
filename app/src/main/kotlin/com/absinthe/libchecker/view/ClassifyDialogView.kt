package com.absinthe.libchecker.view

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.libchecker.bean.AppItem
import com.absinthe.libchecker.constant.GlobalValues
import com.absinthe.libchecker.recyclerview.AppAdapter
import com.absinthe.libchecker.ui.detail.AppDetailActivity

const val EXTRA_PKG_NAME = "EXTRA_PKG_NAME"

class ClassifyDialogView(context: Context) : LinearLayout(context) {

    var adapter = AppAdapter()

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        adapter.setOnItemClickListener { adapter, view, position ->
            val intent = Intent(context, AppDetailActivity::class.java).apply {
                putExtras(Bundle().apply {
                    putString(EXTRA_PKG_NAME, (adapter.getItem(position) as AppItem).packageName)
                })
            }

            val options = ActivityOptions.makeSceneTransitionAnimation(
                (context as AppCompatActivity),
                view,
                "app_card_container"
            )

            if (GlobalValues.isShowEntryAnimation.value!!) {
                context.startActivity(intent, options.toBundle())
            } else {
                context.startActivity(intent)
            }
        }

        val rvList = RecyclerView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            layoutManager = LinearLayoutManager(context)
            adapter = this@ClassifyDialogView.adapter
        }

        addView(rvList)
    }

}