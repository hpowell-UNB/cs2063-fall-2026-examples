package mobiledev.unb.ca.roompersistencelibrarydemo.db.entities

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val num: Int
)
