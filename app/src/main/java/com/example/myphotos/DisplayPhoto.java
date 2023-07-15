package com.example.myphotos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class DisplayPhoto extends AppCompatActivity {

    public static Photo currentPhoto;
    public static AlbumT currentAlbum;
    public static Account acc;

    private Button backButton;
    private Button nextButton;
    private Button addTag;
    private Button delete;
    private Button backtoPhotolist;

    private TextInputLayout tagtypemenu;
    private TextInputLayout textinput;

    private ListView taglist;
    private ArrayAdapter<String> adapter3;
    private ArrayList<String> photoTags; //not sure about this arraylist's purpose.
    private ImageView display;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        //setting up the buttons, listview, etc..
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photodisplay);


        backButton = (Button) findViewById(R.id.backDisplayButton);
        nextButton = (Button) findViewById(R.id.forwardDisplayButton);
        addTag = (Button) findViewById(R.id.addTagTypeButton);
        backtoPhotolist = (Button) findViewById(R.id.backtoPhotolist);
        delete = (Button) findViewById(R.id.deletetag);
        taglist = (ListView) findViewById(R.id.taglist);
        display = (ImageView) findViewById(R.id.photoDisplay);

        //Set up textbox and the textbox with dropdown menu
        tagtypemenu = (TextInputLayout) findViewById(R.id.textdropdown);
        textinput = (TextInputLayout) findViewById(R.id.textinput);

         photoTags = new ArrayList<>();
        readAcc();

        for (String key : currentPhoto.tags.keySet()) {
            for (String val : currentPhoto.tags.get(key)) {
                photoTags.add(key+"="+val);
                System.out.println(key+" "+val);
            }
        }

        adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, photoTags);
        setListAdapter(adapter3);
        taglist.setAdapter(adapter3);


        //When using the back button on the top left corner:
        backtoPhotolist.setOnClickListener(view -> {
            Intent intent = new Intent(this, InsideAlbum.class);
            startActivity(intent);
        });

        //When using back button in the middle that goes to prev photo

        backButton.setOnClickListener(view -> {
            sendUserDataBack();
        });

        //When using next button in middle- going to next photo
        nextButton.setOnClickListener(view -> {

        });

    }

    private void setListAdapter(ArrayAdapter<String> adapter3) {
    }

    private void sendUserDataBack() {
        Intent send = new Intent(DisplayPhoto.this,InsideAlbum.class);
        writeAcc();
        startActivity(send);
    }

    public void writeAcc() {
        try {
            String p = this.getApplicationInfo().dataDir + "/appdata.dat";
            FileOutputStream fos = new FileOutputStream(p);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(acc);
            fos.close();
            oos.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void readAcc(){
        try {
            String p = this.getApplicationInfo().dataDir + "/appdata.dat";
            FileInputStream fis = new FileInputStream(p);
            ObjectInputStream ois = new ObjectInputStream(fis);
            acc = (Account) ois.readObject();
            fis.close();
            ois.close();
        } catch (Exception ignored) {
            ;
        }
    }

}