package com.example.acer.animals;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.acer.animals.data.PetContract;
import com.example.acer.animals.data.PetDbHelper;

public class EditorActivty extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private boolean mPetHasChang=false;

    Uri mCurrentpeturi;
    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    private static int EXISTING_PET_LOADER =0;

    private int mGender = PetContract.PetEntry.GENDER_UNKNOWN;

    private View.OnTouchListener mTouchlistener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPetHasChang=true;
            return false;
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_activty);

        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        mNameEditText.setOnTouchListener(mTouchlistener);
        mBreedEditText.setOnTouchListener(mTouchlistener);
        mWeightEditText.setOnTouchListener(mTouchlistener);
        mGenderSpinner.setOnTouchListener(mTouchlistener);

        setupSpinner();

        Intent intent = getIntent();

        mCurrentpeturi = intent.getData();

        if(mCurrentpeturi ==null){
            setTitle(getString(R.string.title_Activity_Add));
        }
        setTitle(getString(R.string.title_Activity_Edit_pet));

        invalidateOptionsMenu();

        getLoaderManager().initLoader(EXISTING_PET_LOADER, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentpeturi == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }


    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetContract.PetEntry.GENDER_MALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetContract.PetEntry.GENDER_FEMALE;
                    } else {
                        mGender = PetContract.PetEntry.GENDER_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetContract.PetEntry.GENDER_UNKNOWN;
            }
        });
    }

    /**
     * Get user input from editor and save new pet into database.
     */
    private void savePet() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String breedString = mBreedEditText.getText().toString().trim();
        String weightString = mWeightEditText.getText().toString().trim();


        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, nameString);
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, breedString);
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, mGender);

        if (mCurrentpeturi == null
                && TextUtils.isEmpty(nameString)
                && TextUtils.isEmpty(breedString)
                && mGender== 0 ){
            return;
        }

        int weight =0;
        if (!TextUtils.isEmpty(weightString)){
            weight = Integer.parseInt(weightString);
        }
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, weight);


      if(mCurrentpeturi== null){
          Uri uri= getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, values);

          if (uri== null)
          {
              Toast.makeText(this,getString(R.string.not_inserted),Toast.LENGTH_SHORT).show();
          }
          else {
              Toast.makeText(this,getString(R.string.inserted_succefully), Toast.LENGTH_SHORT).show();
          }

          int rowefficted = getContentResolver().update(mCurrentpeturi,values,null,null);

          if (rowefficted==0){
              Toast.makeText(this,getString(R.string.pet_update_failed),Toast.LENGTH_SHORT).show();

          }
          else {
              Toast.makeText(this,getString(R.string.Pet_updated),Toast.LENGTH_SHORT).show();
          }

      }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                savePet();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:

                if (!mPetHasChang){
                    NavUtils.navigateUpFromSameTask(this);
                }

                DialogInterface.OnClickListener discardbutton = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(EditorActivty.this);
                    }
                };

              showonclick(discardbutton);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setMessage(getString(R.string.delete_dialog));
        build.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete();
            }
        });
        build.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog!=null){
                    dialog.dismiss();
                }
            }
        });
    }

    private void delete(){
    if (mCurrentpeturi==null){
        return;
    }

    int rowdeleted = getContentResolver().delete(mCurrentpeturi,null,null);

    if (rowdeleted==0){
        Toast.makeText(this,getString(R.string.delete_failed),Toast.LENGTH_SHORT).show();

    }
    Toast.makeText(this,getString(R.string.delete),Toast.LENGTH_SHORT).show();
        finish();
}


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (!mPetHasChang)
        {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discard = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              finish();
            }
        };

        showonclick(discard);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection ={PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED,
                PetContract.PetEntry.COLUMN_PET_GENDER,
                PetContract.PetEntry.COLUMN_PET_GENDER};

        return new CursorLoader(getApplication(),mCurrentpeturi,projection,null,null,null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursor, Cursor data) {
        if (cursor == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToNext()){

            int name = data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME);
            int breed = data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED);
            int weigth = data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT);
            int gender = data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_GENDER);

            String names =data.getString(name);
            String breeds =data.getString(breed);
            int weigths =data.getInt(weigth);
            int genders =data.getInt(gender);

            mNameEditText.setText(names);
            mBreedEditText.setText(breeds);
            mWeightEditText.setText(weigths);

            switch (genders){
                case PetContract.PetEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                    case PetContract.PetEntry.GENDER_FEMALE:
                        mGenderSpinner.setSelection(2);
                        break;
                    default:
                        mGenderSpinner.setSelection(0);
                        break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mGenderSpinner.setSelection(0);
    }


    private void showonclick(DialogInterface.OnClickListener Dialog){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.unsaved_changes_dialog_msg));
        builder.setPositiveButton(R.string.discard,Dialog);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog al = builder.create();
        al.show();

    }

}
