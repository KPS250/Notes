package com.krazzylabs.notes.controller;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.ImageButton;
import android.widget.LinearLayout;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.krazzylabs.notes.R;

/**
 * Created by DJ-KIRU on 6/12/2017.
 */

public class FragmentMenuDialog extends DialogFragment{

    public ImageButton imageButton_white,imageButton_yellow, imageButton_chrome,imageButton_green,imageButton_pink,imageButton_blue,imageButton_purple,imageButton_grey;
    public View rootView;
    private ISelectedData mCallback;
    LinearLayout ll_share,ll_archive,ll_trash;

    public interface ISelectedData {
        void onSelectedData(String string);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (ISelectedData) activity;
        }
        catch (ClassCastException e) {
            Log.d("MyDialog", "Activity doesn't implement the ISelectedData interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.custom_menu_dialog, container, false);
        //getDialog().setTitle("Simple Dialog");
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        imageButton_white= (ImageButton) rootView.findViewById(R.id.imageButton_white);
        imageButton_white.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.onSelectedData(String.valueOf(getString(R.color.white)));
                Log.d("Color", String.valueOf(getString(R.color.white)));
                getDialog().dismiss();
            }
        });

        imageButton_yellow = (ImageButton) rootView.findViewById(R.id.imageButton_yellow);
        imageButton_yellow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.onSelectedData(String.valueOf(getString(R.color.yellow)));
                Log.d("Color", String.valueOf(getString(R.color.yellow)));
                getDialog().dismiss();
            }
        });

        imageButton_chrome = (ImageButton) rootView.findViewById(R.id.imageButton_chrome);
        imageButton_chrome.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.onSelectedData(String.valueOf(getString(R.color.chrome)));
                Log.d("Color", String.valueOf(getString(R.color.chrome)));
                getDialog().dismiss();
            }
        });

        imageButton_green = (ImageButton) rootView.findViewById(R.id.imageButton_green);
        imageButton_green.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.onSelectedData(String.valueOf(getString(R.color.green)));
                Log.d("Color", String.valueOf(getString(R.color.green)));
                getDialog().dismiss();
            }
        });

        imageButton_pink = (ImageButton) rootView.findViewById(R.id.imageButton_pink);
        imageButton_pink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.onSelectedData(String.valueOf(getString(R.color.pink)));
                Log.d("Color", String.valueOf(getString(R.color.pink)));
                getDialog().dismiss();
            }
        });

        imageButton_blue = (ImageButton) rootView.findViewById(R.id.imageButton_blue);
        imageButton_blue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.onSelectedData(String.valueOf(getString(R.color.blue)));
                Log.d("Color", String.valueOf(getString(R.color.blue)));
                getDialog().dismiss();
            }
        });

        imageButton_purple = (ImageButton) rootView.findViewById(R.id.imageButton_purple);
        imageButton_purple.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.onSelectedData(String.valueOf(getString(R.color.purple)));
                Log.d("Color", String.valueOf(getString(R.color.purple)));
                getDialog().dismiss();
            }
        });

        imageButton_grey = (ImageButton) rootView.findViewById(R.id.imageButton_grey);
        imageButton_grey.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.onSelectedData(String.valueOf(getString(R.color.grey)));
                Log.d("Color", String.valueOf(getString(R.color.grey)));
                getDialog().dismiss();
            }
        });

        ll_share = (LinearLayout) rootView.findViewById(R.id.ll_share);
        ll_share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, CreateNote.note.getTitle());
                startActivity(Intent.createChooser(shareIntent, "Share Note"));
            }
        });

        ll_archive = (LinearLayout) rootView.findViewById(R.id.ll_archive);
        ll_archive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CreateNote.firebaseHelper.archiveNote(CreateNote.note);
                Intent intent = new Intent(rootView.getContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });



        return rootView;
    }

}



