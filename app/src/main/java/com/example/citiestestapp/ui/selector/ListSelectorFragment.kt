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
import com.example.citiestestapp.R
import com.example.citiestestapp.databinding.FragmentSelectorMenuBinding
import com.example.citiestestapp.ui.OnCityListSelectedListener
import com.example.citiestestapp.ui.newList.AddCityListDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ListSelectorFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentSelectorMenuBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var carouselAdapter: ListSelectorAdapter

    private val snapHelper = LinearSnapHelper()
    private val bottomSheetConfigurator: BottomSheetConfigurator = BottomSheetConfigurator()
    private val viewModel: SelectorViewModel by viewModels()
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectorMenuBinding.inflate(inflater, container, false)
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
                binding.fullListNameRecyclerView.text = selectedList.fullName
                (activity as? OnCityListSelectedListener)?.onCityListSelected(selectedList)
                centerSelectedItem()
            }
        )
    }

    private fun setupRecyclerView() {
        val carouselRecyclerView = binding.carouselRecyclerView
        initRecyclerView()
        carouselRecyclerView.post {
            addCenterDecoration()
            centerSelectedItem()
        }
        carouselRecyclerView.addOnScrollListener(createScrollListener())
    }

    private fun initRecyclerView() = binding.carouselRecyclerView.apply {
        adapter = carouselAdapter
        layoutManager = LimitedScrollManager(
            requireContext()
        ) { carouselAdapter.itemCount - 1 }
        snapHelper.attachToRecyclerView(this)
    }

    private fun createScrollListener(): ScrollListener {
        val carouselRecyclerView = binding.carouselRecyclerView
        val layoutManager = carouselRecyclerView.layoutManager as LinearLayoutManager
        return ScrollListener(
            layoutManager = layoutManager,
            snapHelper = snapHelper,
            adapter = carouselAdapter,
            recyclerView = carouselRecyclerView,
            onItemSelected = { selectedItem ->
                binding.fullListNameRecyclerView.text = selectedItem.fullName
                (activity as? OnCityListSelectedListener)?.onCityListSelected(selectedItem)
            }
        )
    }

    private fun setupObservers() {
        viewModel.selectorScreenState
            .onEach { state ->
                val itemsWithSelection = if (state.isNotEmpty() &&
                    state.none { it.isSelected }
                ) {
                    state.mapIndexed { index, item ->
                        item.copy(isSelected = index == 0)
                    }
                    } else {
                        state
                    }

                carouselAdapter.updateItems(itemsWithSelection)

                val selectedItem = itemsWithSelection.find { it.isSelected }
                binding.fullListNameRecyclerView.text = selectedItem?.fullName.orEmpty()

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

    private fun centerSelectedItem() {
        val selectedIndex = carouselAdapter.items.indexOfFirst { it.isSelected }
        if (selectedIndex >= 0) {
            val width = binding.carouselRecyclerView.width
            val itemW = resources.getDimensionPixelSize(R.dimen.carousel_item_size)
            (binding.carouselRecyclerView.layoutManager as LinearLayoutManager?)
                ?.scrollToPositionWithOffset(selectedIndex, (width - itemW) / 2)
        }
    }

    private fun addCenterDecoration() {
        val hasDecoration = (0 until binding.carouselRecyclerView.itemDecorationCount).any { i ->
            binding.carouselRecyclerView.getItemDecorationAt(i) is CenterItemDecoration
        }

        if (!hasDecoration) {
            val width = binding.carouselRecyclerView.width
            val itemW = resources.getDimensionPixelSize(R.dimen.carousel_item_size)
            binding.carouselRecyclerView.addItemDecoration(CenterItemDecoration(itemW, width))
        }
    }

    private fun showAddDialog() {
        AddCityListDialogFragment()
            .show(parentFragmentManager, AddCityListDialogFragment::class.java.simpleName)
    }

    private fun toggleSheet() {
        bottomSheetBehavior?.state =
            if (bottomSheetBehavior?.state == STATE_EXPANDED) {
                STATE_HALF_EXPANDED
            } else {
                STATE_EXPANDED
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}