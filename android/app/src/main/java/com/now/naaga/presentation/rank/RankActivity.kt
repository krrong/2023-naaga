package com.now.naaga.presentation.rank

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.now.domain.model.Rank
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.data.firebase.analytics.RANK_RANK
import com.now.naaga.databinding.ActivityRankBinding
import com.now.naaga.presentation.rank.recyclerview.RankAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankActivity : AppCompatActivity(), AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private lateinit var binding: ActivityRankBinding
    private val viewModel: RankViewModel by viewModels()

    private val rankAdapter = RankAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        initRecyclerView()
        registerAnalytics(this.lifecycle)
        viewModel.fetchMyRank()
        viewModel.fetchRanks()
        subscribe()
        setClickListeners()
    }

    private fun initViewModel() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initRecyclerView() {
        binding.rvRankWholeRanks.apply {
            adapter = rankAdapter
            setHasFixedSize(true)
        }
    }

    private fun subscribe() {
        viewModel.ranks.observe(this) { ranks ->
            updateRank(ranks)
        }
        viewModel.throwable.observe(this) { throwable ->
            Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
            logServerError(RANK_RANK, throwable.code, throwable.message.toString())
        }
    }

    private fun updateRank(places: List<Rank>) {
        rankAdapter.submitList(places)
    }

    private fun setClickListeners() {
        binding.ivRankClose.setOnClickListener {
            finish()
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, RankActivity::class.java)
        }
    }
}
