package com.skillbox.ascent.ui.activities


import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.skillbox.ascent.R
import com.skillbox.ascent.data.activity.Activity
import com.skillbox.ascent.data.activity.DetailedActivity
import com.skillbox.ascent.data.activity.DetailedActivityWithAvatar
import com.skillbox.ascent.databinding.FragmentActivitiesBinding
import com.skillbox.ascent.ui.activities.adapter.ActivitiesListAdapter
import com.skillbox.ascent.utils.*
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.ScaleInAnimator
import kotlin.random.Random

@AndroidEntryPoint
class ActivitiesFragment :
    ViewBindingFragment<FragmentActivitiesBinding>(FragmentActivitiesBinding::inflate) {
    private val viewModel: ActivitiesViewModel by viewModels()
    private var athleteAvatarUrl = ""
    private var flagErrorLoading = false
    private lateinit var toolbar: Toolbar
    private var lazyActivities = mutableListOf<DetailedActivityWithAvatar>()
    private var activities = mutableListOf<DetailedActivityWithAvatar>()
    private var activityAdapter: ActivitiesListAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolBar)
        toolbar.title = getString(R.string.activities)
        initList()
        bindViewModel()
    }

    private fun initList() {
        activityAdapter = ActivitiesListAdapter()
        with(binding.activityList) {
            setHasFixedSize(true)
            adapter = activityAdapter
            addItemDecoration(ItemOffsetDecoration(requireContext()))
            itemAnimator = ScaleInAnimator()  // применим аниматор
        }
    }


    private fun bindViewModel() {
        listenerMenuToolBar(toolbar)
        toolbar.setNavigationOnClickListener { findNavController().navigate(R.id.profileFragment) }
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            activityAdapter.items = emptyList()
            getAllActivities()
        }
        viewModel.avatarAthleteLiveData.observe(viewLifecycleOwner) { athleteAvatarUrl = it }
        viewModel.isLoadingLiveData.observe(viewLifecycleOwner) {
            binding.activitiesProgressBar.isVisible = it
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            flagErrorLoading = true
            viewModel.loadActivitiesFromDb() //если ошибка, то грузим из БД
        }
        viewModel.lazyActivitiesLiveData.observe(viewLifecycleOwner) { listLazy ->
            if (listLazy.isNotEmpty()) lazyActivities = collectDetailedLazyActivities(listLazy)
        }

        viewModel.activitiesLiveData.observe(viewLifecycleOwner) { listDetailActivity ->
            activities = collectDetailedActivityWithAvatar(listDetailActivity)
            activities.addAll(lazyActivities)
            activityAdapter.items = activities
            showSnackBar(flagErrorLoading)
            flagErrorLoading = false
        }

        binding.createActivity.setOnClickListener {
            findNavController().navigate(R.id.createActivityFragment)
        }
    }

    private fun showSnackBar(flagErrorLoading: Boolean) {
        if (!flagErrorLoading) {
            snackBarSuccess(
                binding.swipeRefresh,
                "${resources.getString(R.string.activities_count)}${activities.size}",
                searchBottomView(requireActivity())
            )
        } else {
            showCustomSnackBar()
        }
    }

    private fun getAllActivities() {
        viewModel.getLazyActivities()
        viewModel.loadActivities()
    }

    private fun collectDetailedLazyActivities(
        listLazy: List<Activity>
    ): MutableList<DetailedActivityWithAvatar> {
        val newListLazyActivities = mutableListOf<DetailedActivityWithAvatar>()
        listLazy.forEach {
            val detailedActivityWithAvatar = DetailedActivityWithAvatar(
                Random.nextLong().toString(),
                it.name,
                it.type,
                it.startDateLocal,
                it.elapsedTime,
                it.description,
                it.distance,
                trainer = true,
                commute = true,
                avatarUrl = athleteAvatarUrl,
                totalElevationGain = 0f,
                true
            )
            newListLazyActivities.add(detailedActivityWithAvatar)

        }
        return newListLazyActivities
    }

    //во фрагменте со списком тренировок отображается аватар профиля, в DetailedActivities
    // нет url аватара. Берем Url из БД и создаем новый обьект класс для передачи в recyclerView
    private fun collectDetailedActivityWithAvatar(
        list: List<DetailedActivity>
    ): MutableList<DetailedActivityWithAvatar> {
        val newListDetailedActivities = mutableListOf<DetailedActivityWithAvatar>()
        list.forEach {
            val detailedActivityWithAvatar = DetailedActivityWithAvatar(
                it.id,
                it.name,
                it.type,
                it.startDateLocal,
                it.elapsedTime,
                it.description,
                it.distance,
                it.trainer,
                it.commute,
                athleteAvatarUrl,
                it.totalElevationGain,
                false
            )
            newListDetailedActivities.add(detailedActivityWithAvatar)
        }
        return newListDetailedActivities
    }

    //создаем SnackBar, чтобы отображался сверху экрана
    private fun showCustomSnackBar() {
        val snackBar: Snackbar = Snackbar.make(
            binding.swipeRefresh,
            getString(R.string.cache),
            Snackbar.LENGTH_INDEFINITE
        )
        val snackBarLayout = snackBar.view
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(4, 10, 4, 0)
        snackBarLayout.layoutParams = lp
        snackBar
            .setBackgroundTint(setColor(requireContext(), R.color.snack_failed_back))
            .setActionTextColor(setColor(requireContext(), R.color.black))
            .setTextColor(setColor(requireContext(), R.color.snack_failed_text))
            .setAction("x") {}
            .show()
    }

    override fun onResume() {
        super.onResume()
        statusBottomBar(requireActivity(), true)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRemoving) {
            requireActivity().hideKeyboardAndClearFocus()
        }
    }

}