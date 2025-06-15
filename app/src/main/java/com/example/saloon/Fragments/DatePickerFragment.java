package com.example.saloon.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DatePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog( @Nullable Bundle savedInstanceState ) {
        // Use the current date as the default date in the picker
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get( Calendar.YEAR );
        int month = calendar.get( Calendar.MONTH );
        int day = calendar.get( Calendar.DAY_OF_MONTH );

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog( getActivity() , android.R.style.Theme_Holo_Light_Dialog , ( DatePickerDialog.OnDateSetListener ) getActivity() , year , month , day );
        datePickerDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        datePickerDialog.setTitle( "Set Date" );
        return datePickerDialog;
    }
}
