package com.example.myphotos;

import android.os.Bundle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Photo implements Serializable {
    public String photoRef;
    public String cap;
    public HashMap<String,ArrayList<String>> tags;


    public Photo(String path, String caption) {
        cap = caption;
        photoRef = path;
        tags = new HashMap<>();
    }

    @Override
    public String toString(){
        return cap;
    }
}
