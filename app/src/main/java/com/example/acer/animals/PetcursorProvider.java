package com.example.acer.animals;

import android.content.Context;
import android.database.Cursor;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.acer.animals.data.PetContract;



/**
 * Created by acer on 7/17/2017.
 */

public class PetcursorProvider extends CursorAdapter {



    public PetcursorProvider(Context context, Cursor cursor){
        super(context,cursor,0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewname = (TextView) view.findViewById(R.id.name);
        TextView textViewbreed = (TextView) view.findViewById(R.id.breed);

        int name = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME);
        int breed = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED);

        String finalname= cursor.getString(name);
        String finalbreed = cursor.getString(breed);

        if (TextUtils.isEmpty(finalbreed)){
            finalbreed = context.getString(R.string.gender_unknown);
        }

        textViewname.setText(finalname);
        textViewbreed.setText(finalbreed);
    }
}
