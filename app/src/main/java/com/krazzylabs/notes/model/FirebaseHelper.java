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

    private List<Note> noteList = new ArrayList<>();
    private List<Note> noteListBackup = new ArrayList<>();
    private List<Note> noteListArchive = new ArrayList<>();
    private List<Note> noteListTrash = new ArrayList<>();

    private List<Note> lastSelected = new ArrayList<>();

    private Note lastSelectedNote;

    public FirebaseHelper() {
        setFirebasePersistence();
        setDatabase();
        setMyref(reference);
    }

    public  FirebaseDatabase getDatabase() {
        return database;
    }

    public  void setDatabase() {
        database = FirebaseDatabase.getInstance();
    }

    public  DatabaseReference getMyref() {
        return myref;
    }

    public  void setMyref(String reference) {
        myref = database.getReference(reference);
    }


    public  void setFirebasePersistence() {
        if (!firebasePersistence)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            firebasePersistence = true;
        }
    }

    public List<Note> getNoteListBackup() {
        return noteListBackup;
    }

    public List<Note> getNoteList() {
        return noteList;
    }

    public List<Note> getNoteListArchive() {
        return noteListArchive;
    }

    public List<Note> getNoteListTrash() {
        return noteListTrash;
    }

    public List<Note> getLastSelected() {
        return this.lastSelected;
    }

    public void setLastSelected(ArrayList<Note> lastSelected) {
        this.lastSelected.clear();
        this.lastSelected.addAll(lastSelected);
    }

    public Note getLastSelectedNote() {
        return lastSelectedNote;
    }

    public void setLastSelectedNote(Note lastSelectedNote) {
        this.lastSelectedNote = lastSelectedNote;
    }

    public void updateNoteList(List<Note> listNote){

        noteList.clear();
        noteListBackup.clear();
        noteListArchive.clear();
        noteListTrash.clear();

        for (Note eachNote:new ArrayList<Note>(listNote)) {
            if (eachNote.getStatus() != null) {
                if(eachNote.getStatus().equals("active"))
                    noteList.add(eachNote);
                else if(eachNote.getStatus().equals("archive"))
                    noteListArchive.add(eachNote);
                else if(eachNote.getStatus().equals("trash"))
                    noteListTrash.add(eachNote);
            }

        }

        noteListBackup.addAll(listNote);

    }

    public  void createNote(Note note){

        if(note.getTitle().equals("") && note.getBody().equals("")){

        }else{
            String noteId = myref.push().getKey();

            // pushing user to 'users' node using the userId
            myref.child(noteId).setValue(note);
        }

    }

    public  void updateNote(Note note){

        // If we just need to update the existing Note
        if( note.getTitle().equals("") && note.getBody().equals("")) {

        }else{
            String key = note.getKey();
            Log.d("TEST1",myref.toString());
            Log.d("TEST1",key);
            note.removeKey();
            //this.myref = database.getReference("notes");

            // pushing user to 'users' node using the userId

            this.myref.child(key).setValue(note);

        }

    }

    public  List<Note> getNoteList(DataSnapshot dataSnapshot){

        noteList.clear();

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

        updateNoteList(new ArrayList<Note>(noteList));

        return  noteList;
    }

    public void archiveNote(Note note){
        note.setStatus("archive");
        setLastSelectedNote(note);
        updateNote(note);
    }

    public void trashNote(Note note){
        note.setStatus("trash");
        setLastSelectedNote(new Note(note));
        updateNote(note);
    }

    public  void deleteNote(Note note){
        setLastSelectedNote(note);
        Boolean flag = note.keyExists();
        if(!flag){
            myref = database.getReference("notes");
            myref.child(note.getKey()).setValue(null);
        }
    }

    public void activateNote(){
        getLastSelectedNote().setStatus("active");
        updateNote(getLastSelectedNote());
    }

    public void searchNoteList(String query){

        noteList.clear();
        //Traversal throgh Notes in List
        for(Note note : new ArrayList<>(getNoteListBackup())) {
            if(note.getTitle() != null && note.getTitle().contains(query)
                    || note.getBody()!= null && note.getBody().contains(query)) {
                noteList.add(note);
            }
        }
    }

    public void resetNoteListBackup(){
        noteList.clear();
        noteList.addAll(noteListBackup);
    }

    public void selectedTrash(ArrayList<Integer> list){

        for (int index : list) {
            this.lastSelected.add(new Note(noteList.get(index)));
        }
        for(int index : list){
            trashNote(noteList.get(index));
        }
    }

    public void selectedUndoTrash(){

        try {

            for (Note tempNote : lastSelected) {
                setLastSelectedNote(new Note(tempNote));
                activateNote();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
