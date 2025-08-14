package com.example.citiestestapp.ui.newList

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.citiestestapp.R
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

        initSpinner()
        initCheckBox()
        setupConfirmButton()
        setupCancelButton()

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.create_cities_list_message))
            .setView(binding.root)
            .create()
    }

    private fun initSpinner() {
        val colorNames = resources.getStringArray(R.array.color_names)
        val colorResources = intArrayOf(
            R.color.blue,
            R.color.green,
            R.color.red,
            R.color.orange,
            R.color.violet
        )

        binding.colorSpinnerView.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            colorNames
        )

        binding.colorSpinnerView.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedColor = colorResources.getOrElse(position) { R.color.blue }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
    }

    @SuppressLint("SetTextI18n")
    private fun initCheckBox() {
        viewModel.cities.forEach { city ->
            val checkBox = CheckBox(requireContext()).apply {
                text = "${city.name} (${city.year})"
                setOnCheckedChangeListener { buttonView, isChecked ->
                    handleCitySelection(city, buttonView, isChecked)
                }
            }
            binding.citiesLayout.addView(checkBox)
        }
    }

    private fun handleCitySelection(
        city: CityUi,
        buttonView: CompoundButton,
        isChecked: Boolean
    ) {
        if (isChecked) {
            if (selectedCities.size < MAX_CITIES) {
                selectedCities.add(city)
            } else {
                buttonView.isChecked = false
                showToast(getString(R.string.max_cities_message))
            }
        } else {
            selectedCities.remove(city)
        }
    }

    private fun setupConfirmButton() {
        binding.confirmButton.setOnClickListener {
            val shortName = binding.shortNameEditText.text.toString().trim()
            val fullName = binding.fullNameEditText.text.toString().trim()

            when {
                shortName.isEmpty() || fullName.isEmpty() || selectedCities.isEmpty()
                    -> showToast(getString(R.string.empty_fields_message))

                else -> saveCityList(shortName, fullName)
            }
        }
    }

    private fun setupCancelButton() {
        binding.cancelButton.setOnClickListener { dismiss() }
    }

    private fun saveCityList(shortName: String, fullName: String) {
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

    private fun showToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val MAX_CITIES = 5
    }
}
