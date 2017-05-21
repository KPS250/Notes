package com.krazzylabs.notes.model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kiran Shinde
 * Created by DJ-KIRU-LAPPY on 21/05/2017.
 */

public class FirebaseHelper {

    private static FirebaseDatabase database;
    private static DatabaseReference myref;

    private static Boolean firebasePersistence = false;
    private static String reference = "notes";
    private static String TAG = "FireBaseHelper";

    public FirebaseHelper() {
        setFirebasePersistence();
        setDatabase();
        setMyref(reference);
    }

    public static FirebaseDatabase getDatabase() {
        return database;
    }

    public static void setDatabase() {
        database = FirebaseDatabase.getInstance();
    }

    public static DatabaseReference getMyref() {
        return myref;
    }

    public static void setMyref(String reference) {
        myref = database.getReference(reference);
    }


    public static void setFirebasePersistence() {
        if (!firebasePersistence)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            firebasePersistence = true;
        }
    }

    public static void createNote(Note note){

        if(note.getTitle().equals("") && note.getBody().equals("")){

        }else{
            String noteId = myref.push().getKey();

            // pushing user to 'users' node using the userId
            myref.child(noteId).setValue(note);
        }

    }

    public static void updateNote(Note note){

        // If we just need to update the existing Note
        if( note.getTitle().equals("") && note.getBody().equals("")) {

        }else{
            String key = note.getKey();
            note.removeKey();
            //this.myref = database.getReference("notes");

            // pushing user to 'users' node using the userId
            myref.child(key).setValue(note);
        }

    }

    public static void deleteNote(Note note){
        Boolean flag = note.keyExists();
        if(!flag){
            myref = database.getReference("notes");
            myref.child(note.getKey()).setValue(null);
        }
    }

    public static List<Note> getNoteList(DataSnapshot dataSnapshot){

        List<Note> noteList = new ArrayList<>();

        // Looping into different notes
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            // Getting Key of Leaf Node
            String key = postSnapshot.getKey();

            // Getting Leaf Node Parameters
            Note note = postSnapshot.getValue(Note.class);
            note.setKey(key);
            Log.d(TAG, " KEY : "+ key + " Title: " + note.getTitle() + " Body " + note.getBody());
            noteList.add(note);
        }
        return  noteList;
    }
}
