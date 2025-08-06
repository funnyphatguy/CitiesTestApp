package com.example.citiestestapp.ui.cities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.citiestestapp.databinding.FragmentCityListBinding
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class CitiesListFragment : Fragment() {
    private var _binding: FragmentCityListBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: CitiesViewModel by activityViewModels()

    private val citiesAdapter: CitiesAdapter by lazy { CitiesAdapter() }

    private val itemTouchHelper by lazy { ItemTouchHelper(CitiesSwapCallback(viewModel::swapItems)) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCityListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        observeViewModel()
    }

    private fun setupRecycler() {
        binding.citiesRecyclerView.apply {
            adapter = citiesAdapter
            layoutManager = LinearLayoutManager(requireContext())
            itemTouchHelper.attachToRecyclerView(this)
            addItemDecoration(
                MaterialDividerItemDecoration(
                    requireContext(), DividerItemDecoration.VERTICAL
                ).apply { isLastItemDecorated = true }
            )
        }
    }

    private fun observeViewModel() {
        viewModel.cityList
            .onEach { items ->
                citiesAdapter.updateItems(items)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
