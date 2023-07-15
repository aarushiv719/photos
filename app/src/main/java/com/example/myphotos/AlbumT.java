package com.example.myphotos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class AlbumT implements Serializable {

        public String name;
        public ArrayList<Photo> albumPhoto;

        /**
         *
         * @param Alname album name as string
         */

        public AlbumT (String Alname) {
            name = Alname;
            albumPhoto = new ArrayList<>();

        }
        @Override
        public String toString(){
            return name;
        }

    }

