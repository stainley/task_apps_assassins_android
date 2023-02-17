package ca.app.assasins.taskappsassassinsandroid.common.model;

import androidx.room.ColumnInfo;

import java.io.Serializable;

public class Coordinate implements Serializable {
    @ColumnInfo(name = "LATITUDE")
    private double latitude;
    @ColumnInfo(name = "LONGITUDE")
    private double longitude;

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
