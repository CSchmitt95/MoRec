package de.carloschmitt.morec.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;
import java.util.List;

public  class MovementPattern {
    String name;
    boolean single_window;
    List<List<Sample>> patternList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MovementPattern(String name, boolean single_window) {
        this.name = name;
        this.single_window = single_window;
        this.patternList = new LinkedList<>();
    }

    public String getPatternCount() {
        return (int) Math.floor(patternList.size()/(Data.SAMPLES_PER_SECOND*Data.WINDOW_SIZE_IN_S)) + " (" + patternList.size()+")";
    }

    public void addPattern(List<Sample> pattern){
        patternList.add(pattern);
    }

    @Override
    public String toString() {
        return name;
    }


    public boolean isSingle_window() {
        return single_window;
    }

    public void setSingle_window(boolean single_window) {
        this.single_window = single_window;
    }


}
