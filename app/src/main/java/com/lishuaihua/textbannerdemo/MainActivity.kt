package com.lishuaihua.textbannerdemo

import android.app.Activity
import android.os.Bundle
import com.lishuaihua.textbanner.TextBannerView
import java.util.*

class MainActivity : Activity() {
    private var mTvBanner0: TextBannerView? = null
    private var mTvBanner1: TextBannerView? = null
    private var mTvBanner2: TextBannerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.immersive(this)
        initView()
        initData()
    }

    private fun initView() {
        setContentView(R.layout.activity_main)
        mTvBanner0 = findViewById(R.id.tv_banner0)
        mTvBanner1 = findViewById(R.id.tv_banner1)
        mTvBanner2 = findViewById(R.id.tv_banner2)
    }

    private fun initData() {
        var  mList0 =  ArrayList<String>()
        mList0.add("11111")
        mList0.add("2222222")
        mList0.add("333333333")
        mList0.add("4444444444444")
        mList0.add("55555555555555")
        var mList1 =  ArrayList<String>()
        mList1.add("2222222")
        mList1.add("333333333")
        mList1.add("4444444444444")
        mList1.add("55555555555555")
        mList1.add("11111")
        var   mList2 = ArrayList<String>()
        mList2.add("333333333")
        mList2.add("4444444444444")
        mList2.add("55555555555555")
        mList2.add("11111")
        mList2.add("2222222")
        mTvBanner2!!.setDatas(this, mList0)
        mTvBanner1!!.setDatas(this, mList1)
        mTvBanner0!!.setDatas(this, mList2)
    }

    override fun onResume() {
        super.onResume()
        /**调用startViewAnimator()重新开始文字轮播 */
        mTvBanner2!!.startViewAnimator()
        mTvBanner0!!.startViewAnimator()
        mTvBanner1!!.startViewAnimator()
    }

    override fun onStop() {
        super.onStop()
        /**调用stopViewAnimator()暂停文字轮播，避免文字重影 */
        mTvBanner2!!.stopViewAnimator()
        mTvBanner0!!.stopViewAnimator()
        mTvBanner1!!.stopViewAnimator()
    }
}