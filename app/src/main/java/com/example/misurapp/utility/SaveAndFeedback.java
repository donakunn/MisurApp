package com.example.misurapp.utility;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.example.misurapp.R;
import com.example.misurapp.db.DbManager;
import com.example.misurapp.db.InstrumentsDBSchema;

/**
 * This class save value registered by an instrument and make a toast for feedback
 */
public class SaveAndFeedback {
    public static void saveAndMakeToast(DbManager dbManager, Context context,String sensorUsed, Float valore) {
        dbManager.saveRegisteredValues(InstrumentsDBSchema.BoyscoutTable.TABLENAME,
                null,null,sensorUsed, valore);

        //feedback
        Toast toast = Toast.makeText(context,context.getResources().getString(R.string.salvato) , Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 300);
        toast.show();
    }

}
