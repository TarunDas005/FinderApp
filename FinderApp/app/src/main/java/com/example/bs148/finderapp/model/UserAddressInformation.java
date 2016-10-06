package com.example.bs148.finderapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BS148 on 10/6/2016.
 */

public class UserAddressInformation {
    private List<Result> results = new ArrayList<Result>();
    private String status;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
