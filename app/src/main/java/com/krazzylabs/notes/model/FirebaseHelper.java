package com.krazzylabs.notes.model;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.krazzylabs.notes.R;

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
    private static String reference = "notes/";
    private static String TAG = "FireBaseHelper";

    private List<Note> noteList = new ArrayList<>();
    private List<Note> noteListArchive = new ArrayList<>();
    private List<Note> noteListTrash = new ArrayList<>();
    private List<Note> noteListSearch = new ArrayList<>();

    private List<Note> lastSelected = new ArrayList<>();
    private Note lastSelectedNote;

    private PrefManager prefManager;
    private Context context;

    public FirebaseHelper(Context context) {
        setFirebasePersistence();
        setDatabase();
        //setMyref(reference);
        prefManager = new PrefManager(context);
        setContext(context);

        setReference();
    }

    public  FirebaseDatabase getDatabase() {
        return database;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
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

    public void setReference(){
        reference = "notes/"+prefManager.getUid();
        Log.d("UID", prefManager.getUid());
        myref = database.getReference(reference);
    }

    public  void setFirebasePersistence() {
        if (!firebasePersistence)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            firebasePersistence = true;
        }
    }

    public List<Note> getDefaultNoteList() {

        if(prefManager.getDisplayScreen().equals(context.getString(R.string.NOTE_ACTIVE)))
            return getNoteList();
        else if(prefManager.getDisplayScreen().equals(context.getString(R.string.NOTE_ARCHIVE)))
            return getNoteListArchive();
        else if(prefManager.getDisplayScreen().equals(context.getString(R.string.NOTE_TRASH)))
            return getNoteListTrash();

        return getNoteList();
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
    }

    public String createNote(Note note){

        String noteId = "";
        if(note.getTitle().equals("") && note.getBody().equals("")){

        }else{
             noteId = myref.push().getKey();
             note.setStatus("active");
            // pushing user to 'users' node using the userId
            myref.child(noteId).setValue(note);
        }
        return noteId;

    }

    public  void updateNote(Note note){

        // If we just need to update the existing Note
        if( note.getTitle().equals("") && note.getBody().equals("")) {

        }else{
            String key = note.getKey();
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
            //Log.d(TAG, " KEY : "+ key + " Title: " + note.getTitle() + " Body " + note.getBody());
            noteList.add(note);
        }

        updateNoteList(new ArrayList<Note>(noteList));
        return  noteList;
    }

    public void archiveNote(Note note){
        note.setStatus("archive");
        setLastSelectedNote(new Note(note));
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
        if(flag){
            myref = database.getReference(reference);
            myref.child(note.getKey()).setValue(null);
        }
    }

    public void activateNote(){
        getLastSelectedNote().setStatus("active");
        updateNote(getLastSelectedNote());
    }

    public void undoAction(){
        updateNote(getLastSelectedNote());
    }

    // Selectd Trash
    public void selectedTrash(ArrayList<Integer> list){

        this.lastSelected.clear();
        for (int index : list) {
            this.lastSelected.add(new Note(getDefaultNoteList().get(index)));
        }
        for(int index : list){
            trashNote(getDefaultNoteList().get(index));
        }
    }

    public void selectedUndoTrash(){

        try {

            for (Note tempNote : lastSelected) {
                setLastSelectedNote(new Note(tempNote));
                undoAction();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Selected Archive
    public void selectedArchive(ArrayList<Integer> list){

        this.lastSelected.clear();
        for (int index : list) {
            this.lastSelected.add(new Note(getDefaultNoteList().get(index)));
        }
        for(int index : list){
            archiveNote(getDefaultNoteList().get(index));
        }
    }

    public void selectedUndoArchive(){

        try {

            for (Note tempNote : lastSelected) {
                setLastSelectedNote(new Note(tempNote));
                undoAction();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Selected Archive
    public void selectedDelete(ArrayList<Integer> list){

        this.lastSelected.clear();
        for (int index : list) {
            this.lastSelected.add(new Note(getDefaultNoteList().get(index)));
        }
        for(int index : list){
            deleteNote(getDefaultNoteList().get(index));
        }
    }

    public void selectedUndoDelete(){

        try {

            for (Note tempNote : lastSelected) {
                setLastSelectedNote(new Note(tempNote));
                undoAction();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void selectedRestore(ArrayList<Integer> list){

        this.lastSelected.clear();
        for (int index : list) {
            this.lastSelected.add(new Note(getDefaultNoteList().get(index)));
        }
        for (Note tempNote : lastSelected) {
            setLastSelectedNote(new Note(tempNote));
            activateNote();
        }
    }

    public void selectedUndoRestore(){

        try {

            for (Note tempNote : lastSelected) {
                setLastSelectedNote(new Note(tempNote));
                undoAction();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    // Searching for Note
    public List<Note> searchNoteList(String query){

        noteListSearch.clear();
        //Traversal throgh Notes in List
        for(Note note : new ArrayList<>(getDisplayScreenNote())) {
            if(note.getTitle() != null && note.getTitle().contains(query)
                    || note.getBody()!= null && note.getBody().contains(query)) {
                noteListSearch.add(note);
            }
        }
        return noteListSearch;
    }

    public List<Note> getDisplayScreenNote(){
        if(prefManager.getDisplayScreen().equals(getContext().getString(R.string.NOTE_ACTIVE)))
            return noteList;
        else if(prefManager.getDisplayScreen().equals(getContext().getString(R.string.NOTE_ARCHIVE)))
            return  noteListArchive;
        else
            return noteListTrash;
    }
}
