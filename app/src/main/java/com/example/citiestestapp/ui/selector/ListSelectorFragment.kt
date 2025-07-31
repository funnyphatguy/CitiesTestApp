package com.example.citiestestapp.ui.selector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class ListSelectorFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentCustomMenuBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var carouselAdapter: ListSelectorAdapter

    @Inject
    lateinit var snapHelper: LinearSnapHelper

    @Inject
    lateinit var bottomSheetConfigurator: BottomSheetConfigurator

    private val viewModel: CityListsViewModel by viewModels()
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
        setupAdapter()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupAdapter() {
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
    }

    private fun setupRecyclerView() {
        binding.rvCarousel.apply {
            adapter = carouselAdapter
            layoutManager = LimitedScrollManager(
                requireContext()
            ) { carouselAdapter.items.size }
        }

        snapHelper.attachToRecyclerView(binding.rvCarousel)

        binding.rvCarousel.post {
            addCenterDecoration()
            centerSelectedItem()
        }

        binding.rvCarousel.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    handleIdleState()
                }
            }
        })
    }

    private fun setupObservers() {
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

                centerSelectedItem()
            }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setupClickListeners() {
        binding.icArrowDown.setOnClickListener { toggleSheet() }
    }

    override fun onStart() {
        super.onStart()
        bottomSheetBehavior = bottomSheetConfigurator.configure(this)
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
            (binding.rvCarousel.layoutManager as LinearLayoutManager?)
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
        AddCityListDialogFragment()
            .show(parentFragmentManager, AddCityListDialogFragment::class.java.simpleName)
    }

    private fun toggleSheet() {
        bottomSheetBehavior?.let {
            it.state = if (it.state == BottomSheetBehavior.STATE_EXPANDED)
                BottomSheetBehavior.STATE_HALF_EXPANDED
            else
                BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}