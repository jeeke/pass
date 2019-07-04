package com.example.mytasker.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mytasker.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import static com.example.mytasker.util.Contracts.PICK_IMAGE_REQUEST;

public class PostTaskExtra extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    //    Button btnDatePicker, btnTimePicker;
    TextView txtDate;
    DatePickerDialog dpd;
    TimePickerDialog tpd;
    String date;
    Calendar c;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;

    public PostTaskExtra() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.post_task_extra, container, false);
        txtDate = layout.findViewById(R.id.pick_time);
        layout.findViewById(R.id.pick_image).setOnClickListener(this::chooseImage);
        txtDate.setOnClickListener(this::openDatePicker);
        c = Calendar.getInstance();
        return layout;
    }

    private void chooseImage(View v) {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void openDatePicker(View v) {
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        dpd = DatePickerDialog.newInstance(this, mYear, mMonth, mDay);
        dpd.setMinDate(c);
        dpd.show(getChildFragmentManager(), "Datepickerdialog");
    }

    private void openTimePicker() {
        mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        tpd = TimePickerDialog.newInstance(this, mHour, mMinute, false);
        tpd.show(getChildFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tpd = null;
        dpd = null;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year + ", ";
        openTimePicker();
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        txtDate.setText(date + hourOfDay + "-" + minute + "-" + second);

    }
}
