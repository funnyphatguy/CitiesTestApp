package com.example.citiestestapp.ui.selector

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.citiestestapp.R
import com.example.citiestestapp.databinding.FragmentCustomMenuBinding
import com.example.citiestestapp.model.City
import com.example.citiestestapp.model.CityList
import com.example.citiestestapp.ui.newList.AddCityListDialogFragment
import com.example.citiestestapp.ui.newList.CityListsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class ListSelectorFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCustomMenuBinding? = null
    private val binding get() = _binding ?: error("Binding is null")

    private lateinit var carouselAdapter: ListSelectorAdapter
    private lateinit var viewModel: CityListsViewModel
    private var selectedIndex = 0
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[CityListsViewModel::class.java]
        selectedIndex = 0

        carouselAdapter = ListSelectorAdapter(
            dataset = emptyList(),
            onAddClick = { showAddDialog() },
            onItemClick = { list ->
                val pos = carouselAdapter.dataset.indexOf(list)
                if (pos >= 0) {
                    selectedIndex = pos
                    carouselAdapter.selectedIndex = pos
                    carouselAdapter.notifyDataSetChanged()
                    binding.tvFullListName.text = list.fullName
                    (activity as? OnCityListSelectedListener)?.onCityListSelected(list)
                    centerSelectedItem()
                }
            }
        )

        binding.rvCarousel.apply {
            adapter = carouselAdapter
            layoutManager = LimitedScrollLinearLayoutManager(context)
        }

        LinearSnapHelper().attachToRecyclerView(binding.rvCarousel)

        if (binding.rvCarousel.itemDecorationCount == 0) {
            binding.rvCarousel.post {
                addCenterDecoration()
                centerSelectedItem()
            }
        } else {
            binding.rvCarousel.post { centerSelectedItem() }
        }

        binding.rvCarousel.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var correcting = false

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !correcting) {
                    handleIdleState()
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cityLists.collect { lists ->
                    carouselAdapter.dataset = lists
                    carouselAdapter.notifyDataSetChanged()
                    selectedIndex = selectedIndex.coerceIn(0, lists.size - 1)
                    carouselAdapter.selectedIndex = selectedIndex
                    binding.tvFullListName.text =
                        lists.getOrNull(selectedIndex)?.fullName.orEmpty()
                    binding.rvCarousel.post {
                        updateCenterDecoration()
                        centerSelectedItem()
                    }
                }
            }
        }

        binding.icArrowDown.setOnClickListener { toggleSheet() }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog ?: return
        val bottomSheet =
            dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                ?: return

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet).apply {
            isFitToContents = false
            halfExpandedRatio = 0.5f
            state = BottomSheetBehavior.STATE_HALF_EXPANDED
            isHideable = true
        }

        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet.requestLayout()
    }

    private fun handleIdleState() {
        val lm = binding.rvCarousel.layoutManager as? LinearLayoutManager ?: return
        val snapView = LinearSnapHelper().findSnapView(lm) ?: return
        val snapPos = lm.getPosition(snapView)
        val isAdd = snapPos >= carouselAdapter.dataset.size

        if (isAdd) {
            correctScroll()
        } else if (snapPos != selectedIndex) {
            selectedIndex = snapPos
            carouselAdapter.selectedIndex = snapPos
            carouselAdapter.notifyDataSetChanged()
            binding.tvFullListName.text = carouselAdapter.dataset[snapPos].fullName
            (activity as? OnCityListSelectedListener)
                ?.onCityListSelected(carouselAdapter.dataset[snapPos])
        }
    }

    private fun correctScroll() {
        val target = selectedIndex.coerceIn(0, carouselAdapter.dataset.lastIndex)
        binding.rvCarousel.apply {
            smoothScrollToPosition(target)
            postDelayed({}, 500)
        }
    }

    private fun centerSelectedItem() {
        val width = binding.rvCarousel.width
        val itemW = resources.getDimensionPixelSize(R.dimen.carousel_item_size)
        (binding.rvCarousel.layoutManager as? LinearLayoutManager)
            ?.scrollToPositionWithOffset(selectedIndex, (width - itemW) / 2)
    }

    private fun addCenterDecoration() {
        val width = binding.rvCarousel.width
        val itemW = resources.getDimensionPixelSize(R.dimen.carousel_item_size)
        binding.rvCarousel.addItemDecoration(CenterItemDecoration(itemW, width))
    }

    private fun updateCenterDecoration() {
        for (i in binding.rvCarousel.itemDecorationCount - 1 downTo 0) {
            val deco = binding.rvCarousel.getItemDecorationAt(i)
            if (deco is CenterItemDecoration) {
                binding.rvCarousel.removeItemDecoration(deco)
            }
        }
        addCenterDecoration()
    }

    private fun showAddDialog() {
        AddCityListDialogFragment(getAllCities())
            .show(parentFragmentManager, "AddCityList")
    }

    private fun getAllCities(): List<City> = listOf(
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

    interface OnCityListSelectedListener {
        fun onCityListSelected(cityList: CityList)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCityListSelectedListener) {
            // OK
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toggleSheet() {
        bottomSheetBehavior?.let {
            it.state = if (it.state == BottomSheetBehavior.STATE_EXPANDED)
                BottomSheetBehavior.STATE_HALF_EXPANDED
            else
                BottomSheetBehavior.STATE_EXPANDED
        }
    }

    inner class LimitedScrollLinearLayoutManager(context: Context) :
        LinearLayoutManager(context, HORIZONTAL, false) {

        override fun scrollHorizontallyBy(
            dx: Int,
            recycler: RecyclerView.Recycler?,
            state: RecyclerView.State?
        ): Int {
            val last = findLastVisibleItemPosition()
            val size = carouselAdapter.dataset.size
            return when {
                dx > 0 && last >= size ->
                    super.scrollHorizontallyBy((dx * 0.3f).toInt(), recycler, state)

                else ->
                    super.scrollHorizontallyBy(dx, recycler, state)
            }
        }
    }
}
