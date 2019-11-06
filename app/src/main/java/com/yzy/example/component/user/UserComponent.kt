package com.yzy.example.component.user

import com.alibaba.android.arouter.launcher.ARouter
import com.yzy.example.constants.RouterConstants

class UserComponent {
    companion object{
        fun startDynamicPublish(
            circleId: Long = 0,//发布圈子动态的传参
            worksBean: String? = null//发布本地作品的传参
        ) {
            ARouter.getInstance()
                .build("/user/home")
                .withLong(RouterConstants.User.KEYMAPLAT, circleId)
                .withString(RouterConstants.User.KEYMAPLNG, worksBean)
                .navigation()
        }
    }
}