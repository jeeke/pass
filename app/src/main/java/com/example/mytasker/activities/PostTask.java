package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.badoualy.stepperindicator.StepperIndicator;
import com.example.mytasker.R;
import com.example.mytasker.api.API;
import com.example.mytasker.fragments.PostTaskCat;
import com.example.mytasker.fragments.PostTaskDetail;
import com.example.mytasker.fragments.PostTaskExtra;
import com.example.mytasker.models.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.functions.FirebaseFunctionsException;

import org.json.JSONArray;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static com.example.mytasker.util.Contracts.PICK_IMAGE_REQUEST;

public class PostTask extends BaseActivity {

    StepperIndicator indicator;
    FloatingActionButton fab;
    int currentpage = 0;
    ProgressDialog dlg;
    String title, desc;
    float reward;
    Fragment fragment;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            Log.e("URI", String.valueOf(uri));

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                ImageView imageView = findViewById(R.id.pickedImage);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setImageDrawable(getDrawable(R.drawable.post_done_anim));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        indicator = findViewById(R.id.stepperIndicator);
        Fragment fragment = new PostTaskCat();
        loadFragment(fragment);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        fab = findViewById(R.id.floatingActionButton);
        dlg = new ProgressDialog(this);
        dlg.setTitle("Posting your task, Please Wait....");
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }

    public void loadNextFrag(View view) {
        if (currentpage < 3) {
            indicator.setCurrentStep(++currentpage);
        }
        if (currentpage == 1) {
            fragment = new PostTaskDetail();
//            fragment = new OrdersFrag();
            loadFragment(fragment);
            //Load frag
        } else if (currentpage == 2) {
            title = ((PostTaskDetail) fragment).title.getText().toString();
            desc = ((PostTaskDetail) fragment).description.getText().toString();
            fragment = new PostTaskExtra();
            loadFragment(fragment);
            AnimatedVectorDrawable icon = (AnimatedVectorDrawable) fab.getDrawable();
            icon.start();
            //Load frag and change fab drawable
        } else {
            //done
            reward = Float.parseFloat(((PostTaskExtra) fragment).reward.getText().toString());
            post();
        }
    }

    public void post() {
        dlg.show();
        Map task = Task.toMap(
                false,
                (int) reward,
                25.0,
                25.0,
                new Date().getTime(),
                PostTaskCat.category,
                title,
                desc,
                "mumbai",
                new JSONArray(),
                new JSONArray()
        );
        API api = new API();
        api.createTask(task).addOnCompleteListener(t -> {
            dlg.dismiss();
            if (!t.isSuccessful()) {
                Exception e = t.getException();
                if (e instanceof FirebaseFunctionsException) {
                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                    FirebaseFunctionsException.Code code = ffe.getCode();
                    Object details = ffe.getDetails();
                    Log.e("tag", ffe + "\n" + code + "\n" + details);
                }
                Toast.makeText(this, "Posting Unsuccessful", Toast.LENGTH_SHORT).show();
                Log.e("tag", e + "");
                return;
            }
            Toast.makeText(this, t.getResult().getMessage() + "", Toast.LENGTH_SHORT).show();
        });
    }


}