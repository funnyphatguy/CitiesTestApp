package com.example.citiestestapp.ui.newList

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.citiestestapp.R
import com.example.citiestestapp.app.CitiesApplication
import com.example.citiestestapp.data.CityListRepository
import com.example.citiestestapp.databinding.DialogAddCityListBinding
import com.example.citiestestapp.model.City
import com.example.citiestestapp.model.CityList

class AddCityListDialogFragment(
    private val allCities: List<City>
) : DialogFragment() {

    private var _binding: DialogAddCityListBinding? = null
    private val binding get() = requireNotNull(_binding) { "Binding must not be null" }

    private val selectedCities = mutableListOf<City>()
    private var selectedColor: Int = 0

    private val viewModel: CityListsViewModel by activityViewModels {
        val app = requireActivity().application as CitiesApplication
        CityListsViewModel.provideFactory(
            CityListRepository(app.database.cityListDao())
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddCityListBinding.inflate(
            LayoutInflater.from(requireContext())
        )
        val view = binding.root

        selectedColor = R.color.color_blue

        val colors = listOf(
            getString(R.string.blue_color) to R.color.color_blue,
            getString(R.string.green_color) to R.color.color_green,
            getString(R.string.red_color) to R.color.color_red,
            getString(R.string.orange_color) to R.color.color_orange,
            getString(R.string.violet_color) to R.color.color_purple
        )

        binding.spinnerColor.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            colors.map { it.first }
        )
        binding.spinnerColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedColor = colors[position].second
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        allCities.forEach { city ->
            val checkBox = CheckBox(requireContext()).apply {
                text = "${city.name} (${city.year})"
                setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        if (selectedCities.size < 5) {
                            selectedCities.add(city)
                        } else {
                            buttonView.isChecked = false
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.max_cities_message),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        selectedCities.remove(city)
                    }
                }
            }
            binding.layoutCities.addView(checkBox)
        }

        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnConfirm.setOnClickListener {
            val shortName = binding.etShortName.text.toString().trim()
            val fullName = binding.etFullName.text.toString().trim()
            if (shortName.isEmpty() || fullName.isEmpty() || selectedCities.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.empty_fields_message),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            viewModel.addList(
                CityList(
                    shortName = shortName,
                    fullName = fullName,
                    color = selectedColor,
                    cities = selectedCities.toList()
                )
            )
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.create_cities_list_message))
            .setView(view)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
