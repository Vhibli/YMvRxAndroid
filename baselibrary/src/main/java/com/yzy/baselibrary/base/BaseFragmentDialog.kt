package com.yzy.baselibrary.base

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.yzy.baselibrary.extention.dp2px
import com.yzy.baselibrary.extention.isDoubleClick
import com.yzy.pj.baselibrary.base.dialog.BaseDialogFragment

/**
 *description: Dialog的基类.
 *@date 2019/7/15
 *@author: yzy.
 */
abstract class BaseFragmentDialog : BaseDialogFragment() {

    var mWidth = WRAP_CONTENT
    var mHeight = WRAP_CONTENT
    var mGravity = Gravity.CENTER
    var mOffsetX = 0
    var mOffsetY = 0
    var mAnimation: Int? = null
    var touchOutside: Boolean = true
    var mSoftInputMode: Int = SOFT_INPUT_STATE_ALWAYS_HIDDEN
    var lowerBackground = false // 是否降级背景，例如图片预览的时候不可以降级（设置Activity的透明度）
    lateinit var mContext: Context

    /****** listener ******/
    private var viewLoadedListener: ((View) -> Unit)? = null
    private var showListener: (() -> Unit)? = null
    private var disListener: (() -> Unit)? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStyle()
        val view = inflater.inflate(contentLayout, container, false)
        viewLoadedListener?.invoke(view)
        initView(view)
        initBeforeCreateView(savedInstanceState)
        return view
    }

    /**
     * 内容布局的ResId
     */
    protected abstract val contentLayout: Int

    /**
     * 需要在onCreateView中调用的方法
     */
    protected open fun initBeforeCreateView(savedInstanceState: Bundle?) {

    }

    protected abstract fun initView(view: View)

    /**
     * 防止同时弹出两个dialog
     */
    override fun show(manager: FragmentManager, tag: String?) {
        if (isDoubleClick() || activity?.isFinishing == true) {
            return
        }
        showListener?.invoke()
        setBooleanField("mDismissed", false)
        setBooleanField("mShownByMe", true)
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

    private fun setBooleanField(fieldName: String, value: Boolean) {
        try {
            val field = DialogFragment::class.java.getDeclaredField(fieldName)
            field.isAccessible = true
            field.set(this, value)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        disListener?.invoke()
        super.onDismiss(dialog)
    }

    fun onShow(listener: () -> Unit) {
        showListener = listener
    }

    fun onDismiss(listener: () -> Unit) {
        disListener = listener
    }

    /**
     * 布局加载完成监听事件
     * 用于 获取布局中的 view
     */
    fun onViewLoaded(listener: (View) -> Unit) {
        viewLoadedListener = listener
    }

    /**
     * 设置统一样式
     */
    private fun setStyle() {
        //获取Window
        val window = dialog?.window
        //无标题
        dialog?.requestWindowFeature(STYLE_NO_TITLE)
        // 透明背景
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (lowerBackground) window?.setDimAmount(0F) else window?.setDimAmount(0.5F)// 去除 dialog 弹出的阴影
        dialog?.setCanceledOnTouchOutside(touchOutside)
        //设置宽高
        window!!.decorView.setPadding(0, 0, 0, 0)
        val wlp = window.attributes
        wlp.width = mWidth
        wlp.height = mHeight
        //设置对齐方式
        wlp.gravity = mGravity
        //设置偏移量
        wlp.x = dialog?.context?.dp2px(mOffsetX) ?: 0
        wlp.y = dialog?.context?.dp2px(mOffsetY) ?: 0
        wlp.softInputMode = mSoftInputMode
        //设置动画
        mAnimation?.also { window.setWindowAnimations(it) }
        window.attributes = wlp
    }


}