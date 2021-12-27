package de.carloschmitt.morec.model;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SampleBuffer extends LinkedList<Sample> {
    int max_size;
    int overlap;
    MovementPattern currentMovementPattern;



    public SampleBuffer(int max_size, int overlap){
        this.max_size = max_size;
        this.overlap = overlap;
        currentMovementPattern = null;
    }

    public void setCurrentMovementPattern(MovementPattern mp){
        this.currentMovementPattern = mp;
        this.clear();
    }

    @Override
    public boolean add(Sample sample){
        if(currentMovementPattern == null) return false;
        if(this.size() == max_size) this.removeFirst();
        return super.add(sample);
    }

    public String status(){
        return "Buffer Status: " +  this.size() + "/" + this.max_size;
    }

    public List<Sample> getSamples(){
        return this.subList(this.size()-max_size,this.size()-1);
    }

    public boolean isSaturated(){
        return this.size() == max_size;
    }

    public MovementPattern getCurrentMovementPattern(){
        return currentMovementPattern;
    }
}