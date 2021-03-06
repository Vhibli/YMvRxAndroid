package com.yzy.example.repository

import com.yzy.baselibrary.base.BaseRepository
import com.yzy.example.http.response.BaseResponse
import com.yzy.example.repository.bean.*
import com.yzy.example.repository.service.GankService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class GankRepository : BaseRepository() {
//    private object SingletonHolder {
//        val holder = GankRepository()
//    }
//
//    companion object {
//        val instance = SingletonHolder.holder
//    }
    private val service: GankService =mAPi.getApi(GankService::class.java)

//    suspend fun getAndroidSuspend(
//        pageSize: Int,
//        page: Int
//    ): GankBaseBean<MutableList<GankAndroidBean>> {
//        return service.getAndroidSuspend(pageSize, page)
//    }

    suspend fun banner(page: Int): BaseResponse<MutableList<BannerBean>> {
        return service.banner(page.toString(), "20")
    }

    /**
     * 获取项目标题数据
     */
    suspend fun getProjecTitle(): BaseResponse<ArrayList<ClassifyBean>> {
        return service.getProjecTitle()
    }

    /**
     * 获取项目标题数据
     */
    suspend fun getProjectData(
        pageNo: Int,
        cid: Int = 0,
        isNew: Boolean = false
    ): BaseResponse<PagerResponse<ArrayList<ArticleDataBean>>> {
        return if (isNew) {
            service.getProjecNewData(pageNo)
        } else {
          service.getProjecDataByType(pageNo, cid)
        }
    }


    /**
     * 获取首页文章数据
     */
    suspend fun getHomeData(pageNo: Int): BaseResponse<PagerResponse<MutableList<ArticleDataBean>>> {
        //同时异步请求2个接口，请求完成后合并数据
        return withContext(Dispatchers.IO) {
            val data = async { service.getAritrilList(pageNo) }
            //如果App配置打开了首页请求置顶文章，且是第一页
            if (pageNo == 0) {
                val topData = async { getTopData() }
                data.await().data.datas.addAll(0, topData.await().data)
                data.await()
            } else {
                data.await()
            }
        }
    }

    /**
     * 获取置顶文章数据
     */
    private suspend fun getTopData(): BaseResponse<MutableList<ArticleDataBean>> {
        return service.getTopAritrilList()
    }

}
