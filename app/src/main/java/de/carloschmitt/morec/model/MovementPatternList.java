package de.carloschmitt.morec.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovementPatternList {

     String[] dictionary = {"Gehen", "Stehen", "Laufen", "Schnelles gehen", "Stampfen", "Stolpern", "Balancieren", "Schleichen", "180 Grad Drehung", "Sehr ungünstiges Hinfallen und danach lachen."};
     int counter = 0;
    public static List<MovementPattern> ITEMS;

    public MovementPatternList(){
        ITEMS = new ArrayList<>();
    }

    public  void generateExampleData(){
        for (int i = 0; i < 25; i++){
            addItem(new MovementPattern(dictionary[i%dictionary.length], false));
        }
    }

    private void addItem(MovementPattern item) {
        ITEMS.add(item);
        counter++;
    }

    private void loadListFromDisk(){

    }

    private void saveListToDisk(){

    }
}