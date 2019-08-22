package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytasker.R;
import com.example.mytasker.models.Feed;
import com.example.mytasker.util.Tools;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Random;

public class PostFeed extends BaseActivity {

    ImageView mImage;
    EditText text;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_feed);
        Tools.initMinToolbar(this, "Create Post", false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        mImage = findViewById(R.id.image);
        findViewById(R.id.pick_image).setOnClickListener(v -> pickImage());
        text = findViewById(R.id.text);
        fab = findViewById(R.id.done);
        fab.setOnClickListener(v -> {
            if(mUri!=null){
                uploadImage(mUri);
            }else verifyNCall();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(uploadTask!=null)
        uploadTask.cancel();
    }

    UploadTask uploadTask;
    String imageURL;
    Uri mUri;
    private void uploadImage(Uri uri){
        fab.setClickable(false);
//        fab.setBackgroundTintList(new ColorStateList(R.color.grey_400));
        ProgressBar progressBar = findViewById(R.id.progress);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        Random rand = new Random();
        int n = rand.nextInt(10);
        String path = "images/" + n + "/";
        n = rand.nextInt(10);
        path+= n  + "/";
        path+= new Date().getTime() + uri.getLastPathSegment();
        StorageReference imageRef = storage.getReference().child(path);
        progressBar.setVisibility(View.VISIBLE);

        uploadTask = imageRef.putFile(uri);
        uploadTask.addOnProgressListener(taskSnapshot -> {
            int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
            progressBar.setProgress(progress);
        }).addOnPausedListener(taskSnapshot -> {
            Toast.makeText(this, "Image couldn't be uploaded", Toast.LENGTH_SHORT).show();
            uploadTask.cancel();
        }).addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            Toast.makeText(this, "Image couldn't be uploaded", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> {
            // Handle successful uploads on complete
            progressBar.setVisibility(View.GONE);
            //TODO remove glide or picasso
            imageURL = imageRef.getDownloadUrl().toString();
            verifyNCall();
        });
    }

    ProgressDialog dlg;
    private void verifyNCall() {
//        Toast.makeText(this, imageURL, Toast.LENGTH_SHORT).show();
        if(text.getText().toString().equals("") && imageURL==null){
            Toast.makeText(this, "Both Fields can not be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        dlg = new ProgressDialog(this);
        dlg.setTitle("Posting...");
        dlg.show();
        postFeed();
    }

    public void postFeed() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Date date = new Date();
        Feed feed = new Feed(
                date.getTime(),
                user.getUid(),
                user.getDisplayName(),
                user.getPhotoUrl().toString(),
                imageURL,
                text.getText().toString(),
                1
        );
        DatabaseReference push = FirebaseDatabase.getInstance().getReference().child("Feeds").push();
        push.setValue(feed).addOnSuccessListener(aVoid -> {
            dlg.dismiss();
        }).addOnFailureListener(e -> {
            dlg.dismiss();
            Toast.makeText(PostFeed.this, "Feed can't be posted", Toast.LENGTH_SHORT).show();
        });
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
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mUri = data.getData();
            Picasso.with(this).load(mUri).into(mImage);
            ((ImageView)findViewById(R.id.picker)).setImageResource(R.drawable.ic_edit);
            ((TextView)findViewById(R.id.add_image)).setText("Edit Image");
        }
    }
}
