package com.drewrick.movieapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.drewrick.domain.service.MovieServiceImp
import com.drewrick.movieapp.App
import com.drewrick.movieapp.R
import com.drewrick.movieapp.databinding.ActivityMainBinding
import com.drewrick.movieapp.fragment.FavouritesFragment
import com.drewrick.movieapp.fragment.FilmsFragment
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var movieService: MovieServiceImp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (applicationContext as App).appComponent.inject(this)

        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_main
        )

        val pagerAdapter = PagerAdapter(this)

        binding.vpMovies.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.vpMovies) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.films)
                }
                else -> {
                    tab.text = getString(R.string.favourites)
                }
            }
        }.attach()


        movieService
            .getMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

    }

    class PagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

        private val fragmentsList = arrayListOf(
            FilmsFragment.getInstance(),
            FavouritesFragment.getInstance()
        )

        override fun getItemCount() = fragmentsList.size

        override fun createFragment(position: Int) = fragmentsList[position]
    }
}