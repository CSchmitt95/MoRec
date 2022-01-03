package de.carloschmitt.morec.model;


import java.util.LinkedList;
import java.util.List;

public  class MovementPattern {
    public String name;
    public boolean single_window;
    public List<Tape> tapes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MovementPattern(String name, boolean single_window) {
        this.tapes = new LinkedList<>();
        this.single_window = single_window;
        this.name = name;
    }

    public String getPatternCount() {
        if(single_window) return tapes.size() + " Fenster";
        else {
            int sum_samples = 0;
            for(Tape tape : tapes){
                sum_samples += tape.getSize();
            }
            return (sum_samples/Data.SAMPLES_PER_SECOND) + " Sekunden (" + tapes.size() + " Aufnahmen)";
        }
        //return tapes.size() + " (" + (int) Math.ceil(tapes.size()/(Data.SAMPLES_PER_SECOND*Data.WINDOW_SIZE_IN_S)) +" unique)";
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
