package de.carloschmitt.morec.model;


import android.os.Parcel;
import android.os.Parcelable;

public  class MovementPatternItem implements Parcelable {
    String name;
    int sample_count;
    boolean single_window;

    // TODO:Data


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MovementPatternItem(String name, int sample_count, boolean single_window) {
        this.name = name;
        this.sample_count = sample_count;
        this.single_window = single_window;
        System.out.println("samplecount: " + sample_count);
    }

    public String getSample_count() {
        return String.valueOf(sample_count);
    }

    public void addSample(){
        sample_count++;
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

    /**
     * Parelable stuff
     */

    protected MovementPatternItem(Parcel in) {
        name = in.readString();
        sample_count = in.readInt();
        single_window = in.readBoolean();
    }

    public static final Creator<MovementPatternItem> CREATOR = new Creator<MovementPatternItem>() {
        @Override
        public MovementPatternItem createFromParcel(Parcel in) {
            return new MovementPatternItem(in);
        }

        @Override
        public MovementPatternItem[] newArray(int size) {
            return new MovementPatternItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(name);
        dest.writeInt(sample_count);
        dest.writeBoolean(single_window);
    }

}
