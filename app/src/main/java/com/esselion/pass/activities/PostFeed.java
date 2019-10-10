package com.esselion.pass.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.esselion.pass.R;
import com.esselion.pass.util.Tools;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.UploadTask;

import static com.esselion.pass.util.Cache.getUser;
import static com.esselion.pass.util.Tools.showSnackBar;

public class PostFeed extends BaseActivity {

    ImageView mImage;
    EditText text;
    FloatingActionButton fab;
    boolean fromPortfolio;

    @Override
    protected void onPause() {
        super.onPause();
        if (uploadTask != null)
            uploadTask.cancel();
    }

    UploadTask uploadTask;
    Uri mUri;

    boolean prevCallResolved = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_feed);
        fromPortfolio = getIntent().getBooleanExtra("fromPortfolio", false);
        if (fromPortfolio) {
            Tools.initMinToolbar(this, "Create Portfolio Item");
        } else {
            Tools.initMinToolbar(this, "Create Post");
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        mImage = findViewById(R.id.image);
        findViewById(R.id.pick_image).setOnClickListener(v -> pickImage());
        text = findViewById(R.id.text);
        fab = findViewById(R.id.done);
        fab.setOnClickListener(v -> verifyNCall());
    }

    private void verifyNCall() {
        if (!prevCallResolved && server == null) ;
        else if (text.getText().toString().equals("") && mUri == null)
            showSnackBar(this, "Both Fields can not be empty");
        else
            server.postFeed(getUser(this), fromPortfolio, text.getText().toString(), mImage, mUri, null);
    }


    private void pickImage() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    public static final int PICK_IMAGE = 127;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mUri = data.getData();
            Glide.with(this).load(mUri).into(mImage);
            ((ImageView) findViewById(R.id.picker)).setImageResource(R.drawable.ic_edit);
            ((TextView) findViewById(R.id.add_image)).setText("Edit Image");
        }
    }

    @Override
    public void onServerCallSuccess(int methodId, String title) {
        super.onServerCallSuccess(methodId, title);
        finish();
    }
}
