package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mytasker.R;
import com.example.mytasker.models.Feed;
import com.example.mytasker.util.Tools;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.example.mytasker.util.Cache.getDatabase;
import static com.example.mytasker.util.Cache.getUser;

public class PostFeed extends BaseActivity {

    ImageView mImage;
    EditText text;
    FloatingActionButton fab;
    CheckBox checkBox;
    boolean onPortfolio;

    @Override
    protected void onPause() {
        super.onPause();
        if (uploadTask != null)
            uploadTask.cancel();
    }

    UploadTask uploadTask;
    String imageURL;
    Uri mUri;

    boolean prevCallResolved = true;

    private void uploadImage(Uri uri) {
        dlg = new ProgressDialog(this);
        dlg.setTitle("Uploading Image...");
        dlg.show();
//        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey_400)));
////        fab.setBackgroundTintList(new ColorStateList(R.color.grey_400));
//        ProgressBar progressBar = findViewById(R.id.progress);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        Random rand = new Random();
        int n = rand.nextInt(10);
        String path = "images/" + n + "/";
        n = rand.nextInt(10);
        path += n + "/";
        path += new Date().getTime() + uri.getLastPathSegment();
        StorageReference imageRef = storage.getReference().child(path);
//        progressBar.setVisibility(View.VISIBLE);
        // Get the data from an ImageView as bytes
        mImage.setDrawingCacheEnabled(true);
        mImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) mImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();
        uploadTask = imageRef.putBytes(data);
        uploadTask.addOnProgressListener(taskSnapshot -> {
//            int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
//            progressBar.setProgress(progress);
        }).addOnPausedListener(taskSnapshot -> {
            Toast.makeText(this, "Image couldn't be uploaded", Toast.LENGTH_SHORT).show();
            uploadTask.cancel();
        }).addOnFailureListener(exception -> {
            dlg.dismiss();
            // Handle unsuccessful uploads
            Toast.makeText(this, "Image couldn't be uploaded", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
            // Handle successful uploads on complete
//            progressBar.setVisibility(View.GONE);
            dlg.dismiss();
            //TODO remove glide or picasso
            imageURL = uri1.toString();
            verifyNCall();
        }));
    }

    ProgressDialog dlg;

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
        checkBox = findViewById(R.id.checkBox);
        fab.setOnClickListener(v -> {
            if (!prevCallResolved) return;
            onPortfolio = checkBox.isChecked();
            if (mUri != null) {
                uploadImage(mUri);
            } else verifyNCall();
        });
    }
    private void verifyNCall() {
        if (text.getText().toString().equals("") && imageURL == null) {
            Toast.makeText(this, "Both Fields can not be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        dlg = new ProgressDialog(this);
        dlg.setTitle("Posting...");
        dlg.show();
        postFeed();
    }

    public void postFeed() {
        FirebaseUser user = getUser();
        Date date = new Date();
        Feed feed = new Feed(
                date.getTime(),
                user.getUid(),
                user.getDisplayName(),
                user.getPhotoUrl().toString(),
                imageURL,
                text.getText().toString()
        );
        DatabaseReference push = getDatabase();
        String key = push.child("Feeds").push().getKey();
        feed.setId(key);
        Map updateMap = new HashMap();
        updateMap.put("Feeds/" + key, feed);
        if (onPortfolio)
            updateMap.put("Portfolios/" + user.getUid() + "/" + key, feed);
        updateMap.put("PrevFeeds/" + user.getUid() + "/" + key, feed);
        push.updateChildren(updateMap).addOnCompleteListener(task -> {
            dlg.dismiss();
            if (task.isSuccessful()) {
                finish();
            } else {
                Toast.makeText(PostFeed.this, "Feed could not be posted", Toast.LENGTH_SHORT).show();
            }
            prevCallResolved = true;
        });
        prevCallResolved = false;
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
}
