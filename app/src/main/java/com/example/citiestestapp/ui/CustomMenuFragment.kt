package com.example.citiestestapp.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.citiestestapp.R
import com.example.citiestestapp.data.City
import com.example.citiestestapp.data.CityList
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.widget.TextView
import com.example.citiestestapp.ui.CenterItemDecoration
import androidx.lifecycle.ViewModelProvider

class CustomMenuFragment : BottomSheetDialogFragment() {
    private lateinit var carouselAdapter: CityListCarouselAdapter
    private lateinit var rvCarousel: RecyclerView
    private lateinit var cityListsViewModel: CityListsViewModel
    private var selectedListIndex: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_custom_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cityListsViewModel = ViewModelProvider(requireActivity()).get(CityListsViewModel::class.java)
        rvCarousel = view.findViewById(R.id.rvCarousel)
        val tvFullListName = view.findViewById<TextView>(R.id.tvFullListName)
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
                    tvFullListName.text = cityList.fullName
                    cityListSelectedListener?.onCityListSelected(cityList)
                    centerSelectedItem()
                }
            }
        )
        rvCarousel.adapter = carouselAdapter
        rvCarousel.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        carouselAdapter.selectedIndex = selectedListIndex
        carouselAdapter.notifyDataSetChanged()
        if (rvCarousel.itemDecorationCount == 0) {
            rvCarousel.post {
                val recyclerViewWidth = rvCarousel.width
                val itemWidth = resources.getDimensionPixelSize(R.dimen.carousel_item_size)
                rvCarousel.addItemDecoration(CenterItemDecoration(itemWidth, recyclerViewWidth))
                centerSelectedItem()
            }
        } else {
            rvCarousel.post { centerSelectedItem() }
        }
        rvCarousel.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    tvFullListName.text = cityList.fullName
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
            tvFullListName.text = lists.getOrNull(selectedListIndex)?.fullName ?: ""
            rvCarousel.post {
                updateCenterItemDecoration()
                centerSelectedItem()
            }
        }
    }

    private fun centerSelectedItem() {
        val recyclerViewWidth = rvCarousel.width
        val itemWidth = resources.getDimensionPixelSize(R.dimen.carousel_item_size)
        val layoutManager = rvCarousel.layoutManager as? LinearLayoutManager
        layoutManager?.scrollToPositionWithOffset(selectedListIndex, (recyclerViewWidth - itemWidth) / 2)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? com.google.android.material.bottomsheet.BottomSheetDialog ?: return
        val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) ?: return
        val displayMetrics = resources.displayMetrics
        val halfScreenHeight = (displayMetrics.heightPixels * 0.5).toInt()
        bottomSheet.layoutParams.height = halfScreenHeight
        bottomSheet.requestLayout()
    }

    private fun updateCenterItemDecoration() {
        for (i in rvCarousel.itemDecorationCount - 1 downTo 0) {
            val deco = rvCarousel.getItemDecorationAt(i)
            if (deco is CenterItemDecoration) {
                rvCarousel.removeItemDecoration(deco)
            }
        }
        val recyclerViewWidth = rvCarousel.width
        val itemWidth = resources.getDimensionPixelSize(R.dimen.carousel_item_size)
        rvCarousel.addItemDecoration(CenterItemDecoration(itemWidth, recyclerViewWidth))
    }

    private fun showAddCityListDialog() {
        val allCities = getAllCities()
        AddCityListDialogFragment(allCities) { newList ->
            cityListsViewModel.addList(newList)
            carouselAdapter.notifyDataSetChanged()
            rvCarousel.post {
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
}