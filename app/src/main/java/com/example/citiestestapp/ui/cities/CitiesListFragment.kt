package com.example.citiestestapp.ui.cities

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.citiestestapp.R
import com.example.citiestestapp.databinding.FragmentCityListBinding
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CitiesListFragment : Fragment(R.layout.fragment_city_list) {

    private var _binding: FragmentCityListBinding? = null
    private val binding: FragmentCityListBinding
        get() = requireNotNull(_binding) { "Binding for FragmentCityListBinding must not be null" }

    private lateinit var viewModel: CitiesViewModel
    private val adapter: CitiesAdapter by lazy { CitiesAdapter() }
    private val itemTouchHelper by lazy {
        ItemTouchHelper(CitiesSwapCallback(viewModel::swapItems))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCityListBinding.bind(view)

        viewModel = ViewModelProvider(requireActivity())[CitiesViewModel::class.java]

        setupAdapter()
        setupObservers()
        initDivider()
    }

    private fun setupAdapter() {
        binding.rvCities.adapter = this@CitiesListFragment.adapter
        binding.rvCities.layoutManager = LinearLayoutManager(requireContext())
        itemTouchHelper.attachToRecyclerView(binding.rvCities)
    }

    private fun setupObservers() {
        viewModel.cityList
            .onEach { item ->
                adapter.updateItems(item)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun initDivider() {
        val divider = MaterialDividerItemDecoration(
            requireContext(),
            DividerItemDecoration.VERTICAL
        ).apply {
            isLastItemDecorated = true
        }
        binding.rvCities.addItemDecoration(divider)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

