package com.example.citiestestapp.ui.newList

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.citiestestapp.R
import com.example.citiestestapp.data.database.CityPreset
import com.example.citiestestapp.databinding.DialogAddCityListBinding
import com.example.citiestestapp.model.CityListUi
import com.example.citiestestapp.model.CityUi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCityListDialogFragment : DialogFragment() {
    private var _binding: DialogAddCityListBinding? = null
    private val binding get() = requireNotNull(_binding) { "Binding must not be null" }
    private val selectedCities = mutableListOf<CityUi>()
    private var selectedColor: Int = 0

    private val viewModel: CityListsViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddCityListBinding.inflate(layoutInflater)
        selectedColor = R.color.blue

        val colors = mapOf(
            getString(R.string.blue_color) to R.color.blue,
            getString(R.string.green) to R.color.green,
            getString(R.string.red_color) to R.color.red,
            getString(R.string.orange_color) to R.color.orange,
            getString(R.string.violet_color) to R.color.purple
        )

        val labels = colors.keys.toList()

        binding.colorSpinnerView.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            labels
        )
        binding.colorSpinnerView.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val label = labels[position]
                selectedColor = colors[label] ?: R.color.blue
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        viewModel.cities.forEach { city ->
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
            binding.citiesLayout.addView(checkBox)
        }

        binding.cancelButton.setOnClickListener { dismiss() }
        binding.confirmButton.setOnClickListener {
            val shortName = binding.shortNameEditText.text.toString().trim()
            val fullName = binding.fullNameEditText.text.toString().trim()
            if (shortName.isEmpty() || fullName.isEmpty() || selectedCities.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.empty_fields_message),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            viewModel.addList(
                CityListUi(
                    name = shortName,
                    fullName = fullName,
                    color = selectedColor,
                    cities = selectedCities.toList()
                )
            )
            dismiss()
        }
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.create_cities_list_message))
            .setView(binding.root)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
