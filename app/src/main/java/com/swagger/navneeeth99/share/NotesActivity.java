package com.swagger.navneeeth99.share;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.parse.ParseUser;


public class NotesActivity extends BaseActivity {
    private Button mNewNotesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        super.onCreateDrawer();

        if (ParseUser.getCurrentUser() != null) {
            mNewNotesButton = (Button) findViewById(R.id.addNewNotesBT);
            mNewNotesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddNewNotesDialogFrag postDF = new AddNewNotesDialogFrag();
                    postDF.show(NotesActivity.this.getFragmentManager(), "save data");
                }
            });

            Spinner mLevelSpinner = (Spinner)findViewById(R.id.levelSpinner);
            ArrayAdapter<CharSequence> mLevelAdapter = ArrayAdapter.createFromResource(this, R.array.level_list, android.R.layout.simple_spinner_item);
            mLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mLevelSpinner.setAdapter(mLevelAdapter);

            Spinner mSubjectSpinner = (Spinner)findViewById(R.id.subjectSpinner);
            ArrayAdapter<CharSequence> mSubjectAdapter = ArrayAdapter.createFromResource(this, R.array.subject_list, android.R.layout.simple_spinner_item);
            mSubjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSubjectSpinner.setAdapter(mSubjectAdapter);
        }
    }
}
