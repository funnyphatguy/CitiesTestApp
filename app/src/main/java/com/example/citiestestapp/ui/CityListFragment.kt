package com.example.citiestestapp.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.citiestestapp.R
import com.example.citiestestapp.databinding.FragmentCityListBinding
import com.google.android.material.divider.MaterialDividerItemDecoration

class CityListFragment : Fragment(R.layout.fragment_city_list) {

    private var _binding: FragmentCityListBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentCityListBinding must not be null")


    private lateinit var viewModel: CityListViewModel

    private lateinit var adapter: CityAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCityListBinding.bind(view)

        viewModel =
            androidx.lifecycle.ViewModelProvider(requireActivity())[CityListViewModel::class.java]

        setupAdapter()
        setupObservers()
        setupDragAndDrop()
        initDivider()
    }

    private fun setupAdapter() {
        adapter = CityAdapter(mutableListOf())
        binding.rvCities.adapter = adapter
        binding.rvCities.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupObservers() {
        viewModel.cityList.observe(viewLifecycleOwner) { cities ->
            android.util.Log.d("CityListFragment", "Observer triggered with cities: $cities")
            if (adapter.items != cities) {
                adapter.updateItems(cities)
            } else {
                android.util.Log.d("CityListFragment", "Cities are the same, no adapter update")
            }
        }
    }

    private fun setupDragAndDrop() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
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

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })
        itemTouchHelper.attachToRecyclerView(binding.rvCities)
    }

    private fun initDivider() {
        val dividerItemDecoration = MaterialDividerItemDecoration(
            requireContext(),
            DividerItemDecoration.VERTICAL
        ).apply {
            isLastItemDecorated = true
        }
        binding.rvCities.addItemDecoration(dividerItemDecoration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}