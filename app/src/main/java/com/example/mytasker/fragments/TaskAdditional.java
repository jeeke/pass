package com.example.mytasker.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mytasker.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class TaskAdditional extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    Button btnDatePicker, btnTimePicker;
    TextView txtDate, txtTime;
    DatePickerDialog dpd;
    TimePickerDialog tpd;
    private int mYear, mMonth, mDay, mHour, mMinute;

    public TaskAdditional() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.frag_task_additional, container, false);
//        btnDatePicker = layout.findViewById(R.id.btn_date);
//        btnTimePicker = layout.findViewById(R.id.btn_time);
//        txtDate = layout.findViewById(R.id.in_date);
//        txtTime = layout.findViewById(R.id.in_time);

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onClick(View v) {

        final Calendar c = Calendar.getInstance();
        if (v == btnDatePicker) {

            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            dpd = DatePickerDialog.newInstance(this, mYear, mMonth, mDay);
            dpd.setMinDate(c);
            dpd.show(getChildFragmentManager(), "Datepickerdialog");
        }
        if (v == btnTimePicker) {

            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            tpd = TimePickerDialog.newInstance(this, mHour, mMinute, false);
            tpd.show(getChildFragmentManager(), "Timepickerdialog");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tpd = null;
        dpd = null;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        txtTime.setText(hourOfDay + "-" + minute + "-" + second);

    }
}
