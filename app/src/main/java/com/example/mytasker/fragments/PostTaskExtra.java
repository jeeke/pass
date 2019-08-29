package com.example.mytasker.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mytasker.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import static com.example.mytasker.util.Tools.formatDate;

public class PostTaskExtra extends Fragment implements DatePickerDialog.OnDateSetListener {

    //    Button btnDatePicker, btnTimePicker;
    private TextView txtDate;
    private DatePickerDialog dpd;
    private long date;
    private Calendar c;
    private EditText reward;

    public long getDate() {
        return date;
    }

    public float getReward() {
        return Float.parseFloat(reward.getText().toString());
    }

    public PostTaskExtra() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.post_task_extra, container, false);
        txtDate = layout.findViewById(R.id.pick_time);
        reward = layout.findViewById(R.id.reward);
//        layout.findViewById(R.id.pick_image).setOnClickListener(this::chooseImage);
        txtDate.setOnClickListener(this::openDatePicker);
        c = Calendar.getInstance();
        return layout;
    }

//    private void chooseImage(View v) {
//        Intent intent = new Intent();
//        // Show only images, no videos or anything else
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        // Always show the chooser (if there are multiple options available)
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//    }

    private void openDatePicker(View v) {
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mYear = c.get(Calendar.YEAR);
        dpd = DatePickerDialog.newInstance(this, mYear, mMonth, mDay);
        dpd.setMinDate(c);
        dpd.show(getChildFragmentManager(), "Datepickerdialog");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        dpd = null;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        date = cal.getTime().getTime();
        txtDate.setText(formatDate(date));
    }

}
