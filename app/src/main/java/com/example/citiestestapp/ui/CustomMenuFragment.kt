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

class CustomMenuFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentCustomMenuBinding? = null
    private val binding get() = _binding!!

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
                val index = cityListsViewModel.getAllLists().indexOf(cityList)
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
        binding.rvCarousel.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                val centerX = recyclerView.width / 2
                var minDistance = Int.MAX_VALUE
                var centerPosition = -1
                for (i in 0 until recyclerView.childCount) {
                    val child = recyclerView.getChildAt(i)
                    val childCenterX = (child.left + child.right) / 2
                    val distance = kotlin.math.abs(childCenterX - centerX)
                    if (distance < minDistance) {
                        minDistance = distance
                        centerPosition = recyclerView.getChildAdapterPosition(child)
                    }
                }
                val lists = cityListsViewModel.getAllLists()
                if (centerPosition != -1 && centerPosition != selectedListIndex && centerPosition < lists.size) {
                    selectedListIndex = centerPosition
                    carouselAdapter.selectedIndex = selectedListIndex
                    carouselAdapter.notifyDataSetChanged()
                    val cityList = lists[selectedListIndex]
                    binding.tvFullListName.text = cityList.fullName
                    cityListSelectedListener?.onCityListSelected(cityList)
                }
            }
        })
        cityListsViewModel.cityLists.observe(viewLifecycleOwner) { lists ->
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
        binding.icArrowDown.setOnClickListener {
            toggleBottomSheetState()
        }
    }

    private fun centerSelectedItem() {
        val recyclerViewWidth = binding.rvCarousel.width
        val itemWidth = resources.getDimensionPixelSize(R.dimen.carousel_item_size)
        val layoutManager = binding.rvCarousel.layoutManager as? LinearLayoutManager
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
        AddCityListDialogFragment(allCities) { newList ->
            cityListsViewModel.addList(newList)
            carouselAdapter.notifyDataSetChanged()
            binding.rvCarousel.post {
                updateCenterItemDecoration()
                selectedListIndex = cityListsViewModel.getAllLists().size - 1
                carouselAdapter.selectedIndex = selectedListIndex
                carouselAdapter.notifyDataSetChanged()
                centerSelectedItem()
            }
        }.show(parentFragmentManager, "AddCityListDialog")
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
}