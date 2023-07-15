package com.example.myphotos;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.XmlRes;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


import android.widget.Toast;

public class InsideAlbum extends AppCompatActivity {

    private Button Back;
    private Button addPhoto;
    private ImageView img;
    private ListView photolist;
    private ArrayAdapter<Photo> adapterP;
    private ArrayList<Photo> photosList;
    public static Photo currentPhoto;
    public static AlbumT currentAlbum;
    public static Account acc;
    public Uri selectedImageUri;

    Intent invokeFiles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_album);

        //Setting up listview, arraylists, adapter and other functions
        Back = findViewById(R.id.backbutton);
        addPhoto = findViewById(R.id.addPhotoButton);
        img = findViewById(R.id.thumbnail);
        photolist = findViewById(R.id.photoList);
        photosList = new ArrayList<>();

        readAcc();
        photosList.addAll(currentAlbum.albumPhoto);


        adapterP = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, photosList);
        photolist.setAdapter(adapterP);


        //Pressing back button and going back to list of album folders.
        Back.setOnClickListener(view -> {
            sendUserDataBack();
        });

        //Pressing the add button and adding a new photo into the listview
        //Opens up gallery to choose a picture.

        addPhoto.setOnClickListener(view -> {
            imageChooser();
            writeAcc();
        });

        photolist.setOnItemClickListener((parent, view, position, id) -> {
            registerForContextMenu(photolist);
            openContextMenu(view);
        });
    }

    //Two methods that invokes gallery to choose and image.
    public void imageChooser() {
        invokeFiles = new Intent();
        invokeFiles.setAction(Intent.ACTION_GET_CONTENT);
        invokeFiles.addCategory(Intent.CATEGORY_OPENABLE);
        invokeFiles.setType("image/*");
        startActivityForResult(Intent.createChooser(invokeFiles, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            Uri selectedImageUri = data.getData();
            String getname = getFileName(selectedImageUri, getApplicationContext());
            //if the selected image isnt empty, then add into imageview and update arraylist
            if (null != selectedImageUri) {
                Photo p = new Photo(selectedImageUri.getPath(), getname);
                photosList.add(p);
                currentAlbum.albumPhoto.add(p);
                acc.photos.put(getname,p);
                writeAcc();
                adapterP.notifyDataSetChanged();
                img.setImageURI(selectedImageUri);
            }
        }
    }

    //Two methods that allow menu to function properly
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photomenus, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == R.id.deletephoto) {
            photosList.remove(info.position);
            currentAlbum.albumPhoto.remove(info.position);
            acc.photos.remove(info.position);
            writeAcc();
            adapterP.notifyDataSetChanged();
        }
        if (item.getItemId() == R.id.showP) {
            currentPhoto = (Photo) photolist.getItemAtPosition(info.position);
            sendUserData();
        }

        return super.onContextItemSelected(item);
    }

    //Method used and called to help get file name
    @SuppressLint("Range")
    String getFileName(Uri uri, Context context) {
        String res = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    res = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }

            if(res == null){
                res = uri.getPath();
                int cut = res.lastIndexOf('/');
                if(cut != -1){
                    res = res.substring(cut+1);
                }
            }

        }
        return res;
    }

    private void sendUserData() {
        Intent send = new Intent(InsideAlbum.this, DisplayPhoto.class);
        writeAcc();
        DisplayPhoto.currentPhoto = currentPhoto;
        DisplayPhoto.currentAlbum = currentAlbum;
        com.example.myphotos.DisplayPhoto.acc = acc;
        startActivity(send);
    }

    private void sendUserDataBack() {
        Intent send = new Intent(InsideAlbum.this,Album.class);
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
