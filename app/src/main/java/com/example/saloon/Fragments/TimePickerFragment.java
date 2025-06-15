package com.example.saloon.Fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog( @Nullable Bundle savedInstanceState ) {
        // Use the current time as the default values for the picker
        final Calendar calendar = Calendar.getInstance();
        int hours = calendar.get( Calendar.HOUR_OF_DAY );
        int minutes = calendar.get( Calendar.MINUTE );

        // Create a new instance of TimePickerDialog and return it
        TimePickerDialog timePickerDialog = new TimePickerDialog( getActivity() , android.R.style.Theme_Holo_Light_Dialog , ( TimePickerDialog.OnTimeSetListener ) getActivity() , hours , minutes , DateFormat.is24HourFormat( getActivity() ) );
        timePickerDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        timePickerDialog.setTitle( "Set Time" );
        return timePickerDialog;
    }
}
