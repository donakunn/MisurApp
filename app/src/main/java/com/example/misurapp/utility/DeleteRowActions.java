package com.example.misurapp.utility;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.misurapp.R;
import com.example.misurapp.db.DbManager;
import com.example.misurapp.db.InstrumentRecord;

/**
 * This class describes actions to be performed on delete button press
 */
public class DeleteRowActions {

    /**
     * debug tag
     */
    private final String TAG ="DeleteRowActions";

    /**
     * Activity in which to perform operations
     */
    private Activity activityInterested;

    /**
     * DbManager object for managing operations on database
     */
    private DbManager appDb;

    /**
     * Interested LinearLayout object
     */
    private LinearLayout linearLayout;

    /**
     * name of the tables interested into delete operations.
     */
    private String tableName;

    /**
     * Constructor. Initialize attributes.
     * @param activityInterested Activity that contains delete button
     * @param appDb DbManager object
     * @param linearLayout Interested layout
     * @param tableName name of the table that contains rows.
     */
    public DeleteRowActions(Activity activityInterested, DbManager appDb, LinearLayout linearLayout,
                            String tableName) {
        Log.d(TAG,"Starting delete row actions");
        this.activityInterested = activityInterested;
        this.appDb = appDb;
        this.linearLayout = linearLayout;
        this.tableName = tableName;
    }

    /**
     * Start the animation, delete the row from the table and call deleteAndRedraw()
     * @param v interested View
     * @param record record we want to delete from the table
     */
    public void actionsOnDeleteButtonPress(View v, InstrumentRecord record) {
        Log.d(TAG,"delete button pressed");
        v.startAnimation(AnimationUtils.loadAnimation
                (activityInterested, R.anim.button_click));
        appDb.deleteARow(tableName, record.getId());
        Toast toast = Toast.makeText(activityInterested,
                activityInterested.getResources().getString(R.string.cancellato),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.show();
        deleteAndRedraw(v);
    }

    /**
     * Start the animation and delete row from the view
     * @param v interested View
     */
    private void deleteAndRedraw(View v) {
        Log.d(TAG,"delete and redraw");
        final View row = (View) v.getParent();
        onDeleteAnimation(row);
        linearLayout.postDelayed(new Runnable() {
            public void run() {
                ViewGroup container = ((ViewGroup) row.getParent());
                container.removeView(row);
                container.invalidate();
            }
        }, 500);
    }

    /**
     * Show fade out animation
     * @param row row to fade out
     */
    private void onDeleteAnimation(final View row) {
        Log.d(TAG,"Starting animation");
        Animation fadeout = new AlphaAnimation(1.f, 0.f);
        fadeout.setDuration(500);
        fadeout.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                row.setVisibility(View.GONE);
            }
        });
        row.startAnimation(fadeout);
    }
}
