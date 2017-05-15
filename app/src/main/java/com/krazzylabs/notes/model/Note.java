package com.krazzylabs.notes.model;

/**
 * Created by DJ-KIRU-LAPPY on 15/05/2017.
 */

public class Note {

    String title;
    String body;
    String last_update;

    // Default Constructor
    public Note() {
    }

    // Parameterized Constructor
    public Note(String title, String body, String last_update) {
        this.title = title;
        this.body = body;
        this.last_update = last_update;
    }

    // Getter and Setter

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }
}
