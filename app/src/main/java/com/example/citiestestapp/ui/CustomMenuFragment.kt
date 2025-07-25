package com.example.citiestestapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.citiestestapp.R
import com.example.citiestestapp.data.City
import com.example.citiestestapp.data.CityList
import com.example.citiestestapp.databinding.FragmentCustomMenuBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.citiestestapp.databinding.DialogAddCityListBinding

class CustomMenuFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCustomMenuBinding? = null
    private val binding get() = requireNotNull(_binding) { "Binding must not be null" }

    private lateinit var carouselAdapter: CityListCarouselAdapter
    private lateinit var cityListsViewModel: CityListsViewModel
    private var selectedListIndex: Int = 0
    private var bottomSheetBehavior: com.google.android.material.bottomsheet.BottomSheetBehavior<View>? =
        null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cityListsViewModel = ViewModelProvider(requireActivity()).get(CityListsViewModel::class.java)
        selectedListIndex = 0
        carouselAdapter = CityListCarouselAdapter(
            emptyList(),
            onAddClick = { showAddCityListDialog() },
            onItemClick = { cityList ->
                val lists = carouselAdapter.items
                val index = lists.indexOf(cityList)
                if (index != -1) {
                    selectedListIndex = index
                    carouselAdapter.selectedIndex = selectedListIndex
                    carouselAdapter.notifyDataSetChanged()
                    binding.tvFullListName.text = cityList.fullName
                    cityListSelectedListener?.onCityListSelected(cityList)
                    centerSelectedItem()
                }
            }
        )
        binding.rvCarousel.adapter = carouselAdapter
        binding.rvCarousel.layoutManager = LimitedScrollLinearLayoutManager(requireContext())

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvCarousel)

        carouselAdapter.selectedIndex = selectedListIndex
        carouselAdapter.notifyDataSetChanged()
        if (binding.rvCarousel.itemDecorationCount == 0) {
            binding.rvCarousel.post {
                val recyclerViewWidth = binding.rvCarousel.width
                val itemWidth = resources.getDimensionPixelSize(R.dimen.carousel_item_size)
                binding.rvCarousel.addItemDecoration(
                    CenterItemDecoration(
                        itemWidth,
                        recyclerViewWidth
                    )
                )
                centerSelectedItem()
            }
        } else {
            binding.rvCarousel.post { centerSelectedItem() }
        }

        binding.rvCarousel.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var isScrollCorrectionInProgress = false

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE && !isScrollCorrectionInProgress) {
                    val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                    val snapView = snapHelper.findSnapView(layoutManager) ?: return
                    val snapPosition = layoutManager.getPosition(snapView)
                    val lists = carouselAdapter.items

                    val isAddButton = snapPosition >= lists.size

                    if (isAddButton) {
                        isScrollCorrectionInProgress = true
                        val targetPosition = selectedListIndex.coerceIn(0, lists.size - 1)
                        recyclerView.smoothScrollToPosition(targetPosition)
                        recyclerView.postDelayed({ isScrollCorrectionInProgress = false }, 500)
                    } else if (snapPosition != selectedListIndex && snapPosition < lists.size) {

                        selectedListIndex = snapPosition
                        carouselAdapter.selectedIndex = selectedListIndex
                        carouselAdapter.notifyDataSetChanged()
                        val cityList = lists[selectedListIndex]
                        binding.tvFullListName.text = cityList.fullName
                        cityListSelectedListener?.onCityListSelected(cityList)
                    }
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cityListsViewModel.cityLists.collect { lists ->
                    carouselAdapter.items = lists
                    carouselAdapter.notifyDataSetChanged()
                    if (selectedListIndex >= lists.size) selectedListIndex = lists.size - 1
                    if (selectedListIndex < 0) selectedListIndex = 0
                    carouselAdapter.selectedIndex = selectedListIndex
                    binding.tvFullListName.text = lists.getOrNull(selectedListIndex)?.fullName ?: ""
                    binding.rvCarousel.post {
                        updateCenterItemDecoration()
                        centerSelectedItem()
                    }
                }
            }
        }
        binding.icArrowDown.setOnClickListener {
            toggleBottomSheetState()
        }
    }

    private fun centerSelectedItem() {
        val recyclerViewWidth = binding.rvCarousel.width
        val itemWidth = resources.getDimensionPixelSize(R.dimen.carousel_item_size)
        val layoutManager = binding.rvCarousel.layoutManager as? LinearLayoutManager

        val lists = carouselAdapter.items
        if (selectedListIndex >= lists.size) {
            selectedListIndex = if (lists.isNotEmpty()) lists.size - 1 else 0
        }

        layoutManager?.scrollToPositionWithOffset(selectedListIndex, (recyclerViewWidth - itemWidth) / 2)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? com.google.android.material.bottomsheet.BottomSheetDialog ?: return
        val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) ?: return
        bottomSheetBehavior =
            com.google.android.material.bottomsheet.BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior?.state =
            com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
        bottomSheetBehavior?.addBottomSheetCallback(object :
            com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet.requestLayout()
    }

    private fun updateCenterItemDecoration() {
        for (i in binding.rvCarousel.itemDecorationCount - 1 downTo 0) {
            val deco = binding.rvCarousel.getItemDecorationAt(i)
            if (deco is CenterItemDecoration) {
                binding.rvCarousel.removeItemDecoration(deco)
            }
        }
        val recyclerViewWidth = binding.rvCarousel.width
        val itemWidth = resources.getDimensionPixelSize(R.dimen.carousel_item_size)
        binding.rvCarousel.addItemDecoration(CenterItemDecoration(itemWidth, recyclerViewWidth))
    }

    private fun showAddCityListDialog() {
        val allCities = getAllCities()
        AddCityListDialogFragment(allCities).show(parentFragmentManager, "AddCityListDialog")
    }

    private fun getAllCities(): List<City> {
        return listOf(
            City("Париж", "III век до н.э."),
            City("Вена", "1147 год"),
            City("Берлин", "1237 год"),
            City("Варшава", "1321 год"),
            City("Милан", "1899 год"),
            City("Мадрид", "852 год"),
            City("Рим", "753 год до н.э."),
            City("Лондон", "43 год н.э."),
            City("Прага", "885 год"),
            City("Будапешт", "1873 год")
        )
    }

    interface OnCityListSelectedListener {
        fun onCityListSelected(cityList: CityList)
    }

    private var cityListSelectedListener: OnCityListSelectedListener? = null

    override fun onAttach(context: android.content.Context) {
        super.onAttach(context)
        if (context is OnCityListSelectedListener) {
            cityListSelectedListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        cityListSelectedListener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toggleBottomSheetState() {
        val behavior = bottomSheetBehavior ?: return
        when (behavior.state) {
            com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED -> {
                behavior.state =
                    com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
            }

            else -> {
                behavior.state =
                    com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    inner class LimitedScrollLinearLayoutManager(context: android.content.Context) : LinearLayoutManager(context, HORIZONTAL, false) {
        override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
            val lists = carouselAdapter.items
            if (lists.isEmpty()) return super.scrollHorizontallyBy(dx, recycler, state)

            val lastVisiblePosition = findLastVisibleItemPosition()

            if (dx > 0 && lastVisiblePosition >= lists.size) {
                val limitedDx = (dx * 0.3f).toInt() // Ограничиваем скролл на 70%
                return super.scrollHorizontallyBy(limitedDx, recycler, state)
            }

            return super.scrollHorizontallyBy(dx, recycler, state)
        }
    }
}