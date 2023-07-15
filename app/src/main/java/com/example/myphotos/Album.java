package com.example.myphotos;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.app.AlertDialog;
import android.widget.PopupMenu;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class Album extends AppCompatActivity {

    private static final String ALBUM_NAME = "album_name";
    private Button addAlbum;
    private ListView list;
    private ArrayList<AlbumT> albumlist;
    private ArrayAdapter<AlbumT> adapter;
    private String albumText = "";
    private String editedAlbumText = "";
    public static AlbumT album;
    public static Account acc;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //setting up the button and listview
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        albumlist = new ArrayList<>();
        addAlbum = findViewById(R.id.addAlbum);
        list = findViewById(R.id.albumList);
        readAcc();
        if (acc==null) {
            acc = new Account();
            writeAcc();
        }

        albumlist.addAll(acc.albums);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, albumlist);
        list.setAdapter(adapter);


        //When holding down on an element in the listview, edit ane delete items will appear.
        list.setOnItemClickListener((parent, view, position, id) -> {
            registerForContextMenu(list);
            openContextMenu(view);

        });


        //when pressing Create New Album Button
        addAlbum.setOnClickListener(view -> {
            makeAlert("Create New Album"); //Makes an Alert to Pop-up which allows user to type in the new name for an album.
        });

    }

    //Make the alert that allows input
    private void makeAlert(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(text);

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example,
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                albumText = input.getText().toString().trim();

                if (!albumText.isEmpty()) {
                    AlbumT newAlbum = new AlbumT(albumText);
                    albumlist.add(newAlbum);
                    acc.albums.add(newAlbum);
                    writeAcc();
                    adapter.notifyDataSetChanged();

                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    //Two methods to help display menu and its inner contents, edit, delete and move to work.
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == R.id.delete) {

            for (AlbumT a : albumlist) {
                if (a.name.equals(list.getItemAtPosition(info.position))) {
                    albumlist.remove(info.position);
                    acc.albums.remove(info.position);
                    writeAcc();
                }
            }
            adapter.notifyDataSetChanged();

        }

        if (item.getItemId() == R.id.goAlb) {
            album = (AlbumT) list.getItemAtPosition(info.position);
            sendUserData();
        }

        if (item.getItemId() == R.id.edit) {
            //REUSING AND FIXING UP MAKEALERT TO ACCOUNT FOR EDITING.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edit Album");
            String editAlb = (String) list.getItemAtPosition(info.position).toString();

            // Set up the input
            final EditText input = new EditText(this);
            // Specify the type of input expected; this, for example,
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editedAlbumText = input.getText().toString().trim(); //get your new input you get from textbox

                    if (!editedAlbumText.isEmpty()) {
                        for (AlbumT a : acc.albums) {
                            if (a.name.equals(editAlb)) {
                                AlbumT temp = a;
                                temp.name = editedAlbumText;
                                writeAcc();
                                adapter.notifyDataSetChanged();

                            }
                        }
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }

        return super.onContextItemSelected(item);
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

    private void sendUserData() {
        Intent send = new Intent(Album.this,InsideAlbum.class);
        writeAcc();
        InsideAlbum.acc = acc;
        InsideAlbum.currentAlbum = album;
        startActivity(send);
    }

}




