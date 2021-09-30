package com.celestial.progress.data.model

import androidx.room.TypeConverter
import androidx.room.TypeConverters

class DisplayFormatTC {

    @TypeConverter
    fun displayFormat(format: DisplayFormat): String{
        return format.name
    }

    @TypeConverter
    fun formatStringToEnum(st: String): DisplayFormat{
        for(d in DisplayFormat.values()){
            return if(st == d.name) d else DisplayFormat.DAY
        }
        return DisplayFormat.DAY
    }

    fun getValue(index: Int): DisplayFormat {
        return DisplayFormat.values()[index]
    }
}