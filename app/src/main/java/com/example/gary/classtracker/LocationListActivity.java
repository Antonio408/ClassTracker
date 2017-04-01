package com.example.gary.classtracker;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
/*
created by Gary
class to display available locations and save new ones
 */
public class LocationListActivity extends ListActivity {

    private ArrayAdapter<MyLocation> adapter;
    private ArrayList<MyLocation> myLocations;
    public static final String DATA_FILENAME = "locations.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get locations from file
        myLocations = readData();

        /*
        Created by Gary
        if location is passed in add it to the list and save list
         */

        Intent intent = getIntent();

        if (intent.hasExtra(LocationActivity.NAME_KEY)) {
            String name = intent.getStringExtra(LocationActivity.NAME_KEY);
            double longitude = intent.getDoubleExtra(LocationActivity.LONGITUDE_KEY, 0);
            double latitude = intent.getDoubleExtra(LocationActivity.LATITUDE_KEY, 0);
            MyLocation myLocation = new MyLocation(name, longitude, latitude);
            myLocations.add(myLocation);
            writeData();
        }

        // Setup adapter
        adapter = new ArrayAdapter<MyLocation>(this,
                android.R.layout.simple_list_item_1,
                myLocations);

        setListAdapter(adapter);
    }

    /*
    Created by Gary
   method will retrieve ArrayList<Location> from file class return locations
   */
    private ArrayList<MyLocation> readData() {
        ArrayList<MyLocation> myLocations = new ArrayList<>();
        try {
            Log.i("READ", "entered read");
            FileInputStream fis = openFileInput(DATA_FILENAME);
            ObjectInputStream objectInStream = new ObjectInputStream(fis);
            myLocations =  (ArrayList) objectInStream.readObject();

            fis.close();

        } catch (FileNotFoundException e) {
            // ok if file does not exist
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("READ", "Can not find Data: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return myLocations;
    }

    /*
    Created by Gary
   method will retrieve ArrayList<Location> from Location class and save data to a file
   */
    private void writeData() {
        try {

            FileOutputStream fis = openFileOutput(DATA_FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fis);
            objectOutputStream.writeObject(myLocations);
            fis.close();
            Log.e("writeData", "*************************************************** ");

        } catch (FileNotFoundException e) {
            Log.e("WRITE", "Cannot save data: " + e.getMessage());
            e.printStackTrace();
            //Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("WRITE", "Cannot save data: " + e.getMessage());
        }
    }
}
