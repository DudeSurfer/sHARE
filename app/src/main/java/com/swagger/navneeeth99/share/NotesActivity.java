package com.swagger.navneeeth99.share;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;


public class NotesActivity extends BaseActivity {
    private Button mNewNotesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        super.onCreateDrawer();

        mNewNotesButton = (Button)findViewById(R.id.addNewNotesBT);
        mNewNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewNotesDialogFrag postDF = new AddNewNotesDialogFrag();
                postDF.show(NotesActivity.this.getFragmentManager(), "save data");
            }
        });
    }
}
