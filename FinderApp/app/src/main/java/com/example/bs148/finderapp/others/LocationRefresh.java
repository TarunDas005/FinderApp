package com.example.bs148.finderapp.others;

import android.location.Location;

/**
 * Created by BS148 on 10/5/2016.
 */

public class LocationRefresh {
    private Location location;

    public LocationRefresh(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
