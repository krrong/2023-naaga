package com.now.naaga.presentation.beginadventure

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.now.domain.model.AdventureStatus
import com.now.naaga.R
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.BEGIN_BEGIN_ADVENTURE
import com.now.naaga.data.firebase.analytics.BEGIN_GO_MYPAGE
import com.now.naaga.data.firebase.analytics.BEGIN_GO_SETTING
import com.now.naaga.data.firebase.analytics.BEGIN_GO_UPLOAD
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.databinding.ActivityBeginAdventureBinding
import com.now.naaga.presentation.common.dialog.DialogType
import com.now.naaga.presentation.common.dialog.PermissionDialog
import com.now.naaga.presentation.mypage.MyPageActivity
import com.now.naaga.presentation.onadventure.OnAdventureActivity
import com.now.naaga.presentation.setting.SettingActivity
import com.now.naaga.presentation.upload.UploadActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BeginAdventureActivity : AppCompatActivity(), AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private lateinit var binding: ActivityBeginAdventureBinding
    private val viewModel: BeginAdventureViewModel by viewModels()

    private val onAdventureActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            startLoading()
            fetchInProgressAdventure()
        }

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    Toast.makeText(this, getString(R.string.beginAdventure_precise_access), Toast.LENGTH_SHORT).show()
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    Toast.makeText(this, getString(R.string.beginAdventure_approximate_access), Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {
                    Toast.makeText(this, getString(R.string.beginAdventure_denied_access), Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeginAdventureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startLoading()
        registerAnalytics(this.lifecycle)
        fetchInProgressAdventure()
        requestLocationPermission()
        setClickListeners()
        subscribe()

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun fetchInProgressAdventure() {
        viewModel.fetchAdventure(AdventureStatus.IN_PROGRESS)
    }

    private fun subscribe() {
        viewModel.loading.observe(this) { loading ->
            setLoadingView(loading)
        }
        viewModel.error.observe(this) { error: DataThrowable ->
            Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLoadingView(loading: Boolean) {
        if (!loading) {
            finishLoading()
        }
    }

    private fun startLoading() {
        binding.lottieBeginAdventureLoading.visibility = View.VISIBLE
    }

    private fun finishLoading() {
        binding.lottieBeginAdventureLoading.visibility = View.GONE
    }

    private fun requestLocationPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    private fun setClickListeners() {
        binding.btnBeginAdventureBegin.setOnClickListener {
            logClickEvent(getViewEntryName(it), BEGIN_BEGIN_ADVENTURE)
            checkPermissionAndBeginAdventure()
        }
        binding.btnBeginAdventureUpload.setOnClickListener {
            logClickEvent(getViewEntryName(it), BEGIN_GO_UPLOAD)
            startActivity(UploadActivity.getIntent(this))
        }
        binding.btnBeginAdventureMyPage.setOnClickListener {
            logClickEvent(getViewEntryName(it), BEGIN_GO_MYPAGE)
            startActivity(MyPageActivity.getIntent(this))
        }
        binding.ivBeginAdventureSetting.setOnClickListener {
            logClickEvent(getViewEntryName(it), BEGIN_GO_SETTING)
            startActivity(SettingActivity.getIntent(this))
        }
    }

    private fun checkPermissionAndBeginAdventure() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            PermissionDialog(DialogType.LOCATION).show(supportFragmentManager)
        } else {
            checkLocationPermissionInStatusBar()
        }
    }

    private fun checkLocationPermissionInStatusBar() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            Toast.makeText(this, GPS_TURN_ON_MESSAGE, Toast.LENGTH_SHORT).show()
        } else {
            onAdventureActivityLauncher.launch(getIntentWithAdventureOrWithout())
        }
    }

    private fun getIntentWithAdventureOrWithout(): Intent {
        val existingAdventure = viewModel.adventure.value

        return if (existingAdventure == null) {
            OnAdventureActivity.getIntent(this)
        } else {
            OnAdventureActivity.getIntentWithAdventure(this, existingAdventure)
        }
    }

    companion object {
        private const val GPS_TURN_ON_MESSAGE = "GPS 설정을 켜주세요"

        fun getIntent(context: Context): Intent {
            return Intent(context, BeginAdventureActivity::class.java)
        }
    }
}
