package com.krazzylabs.notes.controller.list;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
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

public class NotesAdapter extends SelectableAdapter<NotesAdapter.MyViewHolder> {

    private List<Note> noteList;
    public static CardView mCardView;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, body, last_update, subtitle;
        View selectedOverlay;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            body = (TextView) view.findViewById(R.id.body);
            //last_update = (TextView) view.findViewById(R.id.last_update);
            selectedOverlay = itemView.findViewById(R.id.selected_overlay);

            // Loading Font Face
            Typeface tf = Typeface.createFromAsset(title.getContext().getAssets(), "fonts/RobotoSlab-Bold.ttf");
            title.setTypeface(tf);

            Typeface tf1 = Typeface.createFromAsset(body.getContext().getAssets(), "fonts/RobotoSlab-Light.ttf");
            body.setTypeface(tf1);

        }
    }

    public NotesAdapter(List<Note> noteList) {
        this.noteList = noteList;
    }

    @Override
    public NotesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notelist_card, parent, false);



        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotesAdapter.MyViewHolder holder, int position) {

        Note note = noteList.get(position);
        Log.d("SUBSTRING" , note.getTitle().length() + ":" + note.getBody().length());


        if(note.getTitle().length()==0)
            holder.title.setVisibility(View.GONE);
        else if(note.getTitle().length()>=15){
            holder.title.setText(note.getTitle().substring(0, 15)+"...");
        } else
            holder.title.setText(note.getTitle());

        if(note.getBody().length()==0)
            holder.body.setVisibility(View.GONE);
        else if(note.getBody().length()>=30) {
            holder.body.setText(note.getBody().substring(0, 30)+"...");
        } else
            holder.body.setText(note.getBody());

        mCardView = (CardView) holder.itemView.findViewById(R.id.card_view);
        mCardView.setCardBackgroundColor(Color.parseColor(note.getColour()));

        //holder.subtitle.setText(note.getSubtitle() + ", which is " + (item.isActive() ? "active" : "inactive"));
        //holder.last_update.setText(note.getLast_update());

        // Span the item if active
        final ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
            sglp.setFullSpan(note.getIsActive());
            holder.itemView.setLayoutParams(sglp);
        }

        // Highlight the item if it's selected
        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}