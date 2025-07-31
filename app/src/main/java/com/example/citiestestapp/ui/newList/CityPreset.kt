package com.example.citiestestapp.ui.newList

import com.example.citiestestapp.model.CityUi

enum class CityPreset(val cityName: String, val foundingYear: String) {
    PARIS("Париж", "III век до н.э."),
    VIENNA("Вена", "1147 год"),
    BERLIN("Берлин", "1237 год"),
    WARSAW("Варшава", "1321 год"),
    MILAN("Милан", "1899 год"),
    MADRID("Мадрид", "852 год"),
    ROME("Рим", "753 год до н.э."),
    LONDON("Лондон", "43 год н.э."),
    PRAGUE("Прага", "885 год"),
    BUDAPEST("Будапешт", "1873 год");

    fun toCityUi(): CityUi = CityUi(cityName, foundingYear)
}