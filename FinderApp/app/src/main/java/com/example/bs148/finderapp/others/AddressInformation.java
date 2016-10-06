package com.example.bs148.finderapp.others;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by BS148 on 10/3/2016.
 */

public class AddressInformation implements Parcelable{
    private String streetName;
    private String cityName;
    private String stateName;
    private String countryName;
    private double lattitude;
    private double longitude;


    protected AddressInformation(Parcel in) {
        streetName = in.readString();
        cityName = in.readString();
        stateName = in.readString();
        countryName = in.readString();
        lattitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<AddressInformation> CREATOR = new Creator<AddressInformation>() {
        @Override
        public AddressInformation createFromParcel(Parcel in) {
            return new AddressInformation(in);
        }

        @Override
        public AddressInformation[] newArray(int size) {
            return new AddressInformation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(streetName);
        dest.writeString(cityName);
        dest.writeString(stateName);
        dest.writeString(countryName);
        dest.writeDouble(lattitude);
        dest.writeDouble(longitude);
    }


    public AddressInformation(String streetName, String cityName, String stateName, String countryName, double lattitude, double longitude) {
        this.streetName = streetName;
        this.cityName = cityName;
        this.stateName = stateName;
        this.countryName = countryName;
        this.lattitude = lattitude;
        this.longitude = longitude;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
