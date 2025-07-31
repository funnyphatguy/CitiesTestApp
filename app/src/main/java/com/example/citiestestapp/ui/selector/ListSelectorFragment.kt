package com.example.citiestestapp.ui.selector

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.citiestestapp.R
import com.example.citiestestapp.databinding.FragmentCustomMenuBinding
import com.example.citiestestapp.ui.OnCityListSelectedListener
import com.example.citiestestapp.ui.newList.AddCityListDialogFragment
import com.example.citiestestapp.ui.newList.CityListsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ListSelectorFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentCustomMenuBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var carouselAdapter: ListSelectorAdapter
    private lateinit var viewModel: CityListsViewModel
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private lateinit var snapHelper: LinearSnapHelper

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

        carouselAdapter = ListSelectorAdapter(
            onAddClick = { showAddDialog() },
            onItemClick = { selectedList ->
                viewModel.onItemClick(selectedList)

                val updatedItems = carouselAdapter.items.map { item ->
                    item.copy(isSelected = item.id == selectedList.id)
                }

                carouselAdapter.updateItems(updatedItems)
                binding.tvFullListName.text = selectedList.fullName
                (activity as? OnCityListSelectedListener)?.onCityListSelected(selectedList)
                centerSelectedItem()
            }
        )

        binding.rvCarousel.apply {
            adapter = carouselAdapter
            layoutManager = LimitedScrollLinearLayoutManager(context)
        }

        snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvCarousel)

        binding.rvCarousel.post {
            addCenterDecoration()
            centerSelectedItem()
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

        viewModel.selectorScreenState
            .onEach { state ->
                val itemsWithSelection = if (state.isNotEmpty()) {
                    val hasSelected = state.any { it.isSelected }
                    if (!hasSelected) {
                        state.mapIndexed { index, item ->
                            item.copy(isSelected = index == 0)
                        }
                    } else {
                        state
                    }
                } else {
                    state
                }

                carouselAdapter.updateItems(itemsWithSelection)

                val selectedItem = itemsWithSelection.find { it.isSelected }
                binding.tvFullListName.text = selectedItem?.fullName.orEmpty()

                binding.rvCarousel.postDelayed({
                    centerSelectedItem()
                }, 100)

            }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

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
        val snapView = snapHelper.findSnapView(lm) ?: return
        val snapPos = lm.getPosition(snapView)
        val isAdd = snapPos >= carouselAdapter.items.size

        if (isAdd) {
            correctScroll()
        } else {
            val selectedItem = carouselAdapter.items[snapPos]
            val currentSelected = carouselAdapter.items.find { it.isSelected }

            if (currentSelected?.id != selectedItem.id) {
                val updatedItems = carouselAdapter.items.map { item ->
                    item.copy(isSelected = item.id == selectedItem.id)
                }

                carouselAdapter.updateItems(updatedItems)
                binding.tvFullListName.text = selectedItem.fullName
                (activity as? OnCityListSelectedListener)?.onCityListSelected(selectedItem)
            }
        }
    }

    private fun correctScroll() {
        val selectedIndex = carouselAdapter.items.indexOfFirst { it.isSelected }
        val target = selectedIndex.coerceIn(0, carouselAdapter.items.lastIndex)
        binding.rvCarousel.smoothScrollToPosition(target)
    }

    private fun centerSelectedItem() {
        val selectedIndex = carouselAdapter.items.indexOfFirst { it.isSelected }
        if (selectedIndex >= 0) {
            val width = binding.rvCarousel.width
            val itemW = resources.getDimensionPixelSize(R.dimen.carousel_item_size)
            (binding.rvCarousel.layoutManager as? LinearLayoutManager)
                ?.scrollToPositionWithOffset(selectedIndex, (width - itemW) / 2)
        }
    }

    private fun addCenterDecoration() {
        val hasDecoration = (0 until binding.rvCarousel.itemDecorationCount).any { i ->
            binding.rvCarousel.getItemDecorationAt(i) is CenterItemDecoration
        }

        if (!hasDecoration) {
            val width = binding.rvCarousel.width
            val itemW = resources.getDimensionPixelSize(R.dimen.carousel_item_size)
            binding.rvCarousel.addItemDecoration(CenterItemDecoration(itemW, width))
        }
    }

    private fun showAddDialog() {
        AddCityListDialogFragment.getInstance()
            .show(parentFragmentManager, "AddCityList")
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
            val size = carouselAdapter.items.size
            return when {
                dx > 0 && last >= size ->
                    super.scrollHorizontallyBy((dx * 0.3f).toInt(), recycler, state)
                else ->
                    super.scrollHorizontallyBy(dx, recycler, state)
            }
        }
    }
}
