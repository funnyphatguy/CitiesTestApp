package com.example.citiestestapp.ui.cities

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.citiestestapp.R
import com.example.citiestestapp.databinding.FragmentCityListBinding
import com.google.android.material.divider.MaterialDividerItemDecoration

class CitiesListFragment : Fragment(R.layout.fragment_city_list) {

    private var _binding: FragmentCityListBinding? = null
    private val binding: FragmentCityListBinding
        get() = requireNotNull(_binding) { "Binding for FragmentCityListBinding must not be null" }

    private lateinit var viewModel: CitiesViewModel
    private lateinit var adapter: CitiesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCityListBinding.bind(view)

        viewModel = ViewModelProvider(requireActivity())[CitiesViewModel::class.java]

        setupAdapter()
        setupObservers()
        setupDragAndDrop()
        initDivider()
    }

    private fun setupAdapter() {
        adapter = CitiesAdapter(mutableListOf())
        binding.rvCities.apply {
            adapter = this@CitiesListFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObservers() {
        viewModel.cityList.observe(viewLifecycleOwner) { cities ->
            adapter.updateItems(cities)
        }
    }

    private fun setupDragAndDrop() {
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val from = viewHolder.bindingAdapterPosition
                val to = target.bindingAdapterPosition

                adapter.swapItems(from, to)
                viewModel.swapItems(from, to)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit
        }

        ItemTouchHelper(callback).attachToRecyclerView(binding.rvCities)
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
