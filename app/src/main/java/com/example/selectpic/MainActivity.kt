package com.example.selectpic

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearSmoothScroller
import com.example.selectpic.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val imageList = listOf(
            R.drawable.bg_in_main_01,
            R.drawable.bg_in_main_02,
            R.drawable.bg_in_main_03,
            R.drawable.bg_in_main_04,
            R.drawable.bg_in_main_05,
            R.drawable.bg_in_main_06
        )

        setupViewPager(imageList)
        autoScrollViewPager(imageList.size)
    }

    private fun setupViewPager(images: List<Int>) {
        val adapter = ImageInMainAdapter(images)
        binding.viewPager.adapter = adapter
        binding.dotsIndicator.setViewPager2(binding.viewPager)
    }

    private fun autoScrollViewPager(itemCount: Int) {
        lifecycleScope.launch {
            while (true) {
                delay(3000)
                val nextItem = (binding.viewPager.currentItem + 1) % itemCount
                smoothScrollToItem(nextItem)
            }
        }
    }

    private fun smoothScrollToItem(item: Int) {
        val recyclerView = binding.viewPager.getChildAt(0) as androidx.recyclerview.widget.RecyclerView
        val smoothScroller = object : LinearSmoothScroller(this@MainActivity) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return 150f / displayMetrics.densityDpi
            }
        }

        smoothScroller.targetPosition = item
        recyclerView.layoutManager?.startSmoothScroll(smoothScroller)
    }
}
