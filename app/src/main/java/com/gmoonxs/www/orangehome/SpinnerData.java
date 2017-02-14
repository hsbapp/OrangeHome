package com.gmoonxs.www.orangehome;

/**
 * Created by 威 on 2016/7/27.
 */
public class SpinnerData {
    private int id  = 0;
    private String name = "";


    public SpinnerData() {
        id=0;
        name = "";
    }

    public SpinnerData(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {

        return name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
