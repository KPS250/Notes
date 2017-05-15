package com.krazzylabs.notes.controller.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.krazzylabs.notes.R;
import com.krazzylabs.notes.model.Note;

import java.util.List;

/**
 * Created by DJ-KIRU-LAPPY on 15/05/2017.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private List<Note> noteList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, body, last_update;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            body = (TextView) view.findViewById(R.id.body);
            last_update = (TextView) view.findViewById(R.id.last_update);
        }
    }

    public NotesAdapter(List<Note> noteList) {
        this.noteList = noteList;
    }

    @Override
    public NotesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notelist_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotesAdapter.MyViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.title.setText(note.getTitle());
        holder.body.setText(note.getBody());
        holder.last_update.setText(note.getLast_update());
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
