package com.swagger.navneeeth99.share;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Benjamin on 26/6/15.
 */
public class NotesDetailActivity extends BaseActivity {
    private String mNotesTopic;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_detail);
        super.onCreateDrawer();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NotesActivity.DISPLAY_NOTES_DETAILS && resultCode == RESULT_OK){
            data.getData();
        }
    }
}
