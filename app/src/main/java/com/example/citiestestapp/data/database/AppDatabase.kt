package com.example.citiestestapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.citiestestapp.R
import com.example.citiestestapp.model.CityListEntity
import com.example.citiestestapp.model.CityListMapper.gson
import com.example.citiestestapp.model.CityUi
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi

@Database(entities = [CityListEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityListDao(): CityListDao

    companion object {

        private const val DB_NAME = "cities_app_db"

        private val defaultItems = listOf(
            CityUi("Париж", "III век до н.э."),
            CityUi("Вена", "1147 год"),
            CityUi("Берлин", "1237 год"),
            CityUi("Варшава", "1321 год"),
            CityUi("Милан", "1899 год")
        )

        @OptIn(ExperimentalUuidApi::class)
        fun create(context: Context): AppDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DB_NAME
        )
            .addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    val items = CityListEntity(
                        id = UUID.randomUUID().toString(),
                        name = R.string.europe.toString(),
                        fullName = R.string.europe_cities.toString(),
                        color = R.color.color_blue,
                        cities = gson.toJson(defaultItems)
                    )

                    db.execSQL(
                        "INSERT INTO city_lists (id, name, fullName, color, cities) " +
                                "VALUES ('${items.id}', '${items.name}', '${items.fullName}', '${items.color}', '${items.cities}')"
                    )
                    super.onCreate(db)
                }
            })
            .build()
    }
}