package com.zetzaus.criminalintent.database;

import java.util.Date;
import java.util.UUID;

import androidx.room.TypeConverter;

public class CrimeTypeConverter {

    @TypeConverter
    public long fromDate(Date date) {
        return date.getTime();
    }

    @TypeConverter
    public Date toDate(long timeLong) {
        return new Date(timeLong);
    }

    @TypeConverter
    public String fromUUID(UUID id) {
        return id.toString();
    }

    @TypeConverter
    public UUID toUUID(String id) {
        return UUID.fromString(id);
    }
}
