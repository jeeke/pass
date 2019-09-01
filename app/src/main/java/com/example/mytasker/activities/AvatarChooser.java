package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.mytasker.R;
import com.example.mytasker.adapters.AvatarAdapter;
import com.example.mytasker.util.Cache;
import com.example.mytasker.util.Contracts;
import com.example.mytasker.util.Tools;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Random;

import static com.example.mytasker.util.Cache.getUser;

public class AvatarChooser extends AppCompatActivity implements AvatarAdapter.RecyclerViewClickListener {

    public static final int PICK_IMAGE_AVATAR = 128;
    RecyclerView mRecyclerView;
    boolean prevCallResolved = true;
    ProgressDialog dlg;
    UploadTask uploadTask;
    String imageURL;
    Uri mUri;
    ImageView mImage;
    AvatarAdapter adapter;
    ImageView prevImage;
    boolean picked = false;
    int pickedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_chooser);
        mRecyclerView = findViewById(R.id.recyclerView);
        adapter = new AvatarAdapter(this);
        mRecyclerView.setAdapter(adapter);
        Tools.initMinToolbar(this, "EDIT IMAGE", false);
        findViewById(R.id.update_image).setOnClickListener(v -> {
            if (pickedPosition == -1)
                Toast.makeText(this, "Please pick an image", Toast.LENGTH_SHORT).show();
            else uploadImage();
        });
    }

    private void updateImage() {
        if (!prevCallResolved) {
            dlg.dismiss();
            return;
        }
        FirebaseUser user = getUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(imageURL))
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    prevCallResolved = true;
                    dlg.dismiss();
                    if (task.isSuccessful()) {
                        Cache.mUser = null;
                        Log.d("Avatar Chooser", "User profile image updated.");
                        finish();
                    } else
                        Toast.makeText(this, "Image could not be updated", Toast.LENGTH_SHORT).show();

                });
        prevCallResolved = false;
    }

    private void uploadImage() {
        dlg = new ProgressDialog(this);
        dlg.setTitle("Updating Image...");
        dlg.show();
        if (!prevCallResolved) {
            dlg.dismiss();
            return;
        }
        if (pickedPosition != 0) {
            imageURL = Contracts.avatars[pickedPosition - 1];
            updateImage();
        } else if (mUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            Random rand = new Random();
            int n = rand.nextInt(10);
            String path = "images/" + n + "/";
            n = rand.nextInt(10);
            path += n + "/";
            path += new Date().getTime() + mUri.getLastPathSegment();
            StorageReference imageRef = storage.getReference().child(path);
            mImage.setDrawingCacheEnabled(true);
            mImage.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) mImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();
            uploadTask = imageRef.putBytes(data);
            uploadTask.addOnProgressListener(taskSnapshot -> {
            }).addOnPausedListener(taskSnapshot -> {
                prevCallResolved = true;
                Toast.makeText(this, "Image couldn't be uploaded", Toast.LENGTH_SHORT).show();
                uploadTask.cancel();
            }).addOnFailureListener(exception -> {
                prevCallResolved = true;
                dlg.dismiss();
                uploadTask.cancel();
                Toast.makeText(this, "Image couldn't be uploaded", Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                prevCallResolved = true;
                dlg.dismiss();
                imageURL = uri1.toString();
                updateImage();
            }));
            prevCallResolved = false;
        } else {
            dlg.dismiss();
            Toast.makeText(this, "Please pick and image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(ImageView view, int position, boolean selected) {
        if (position == 0 && !picked) {
            mImage = view;
            pickImage();
        }
        if (selected) {
            pickedPosition = position;
        }
        if (prevImage != null) prevImage.setBackground(null);
        view.setBackground(getDrawable(R.drawable.avatar_selected));
        prevImage = view;
    }

    private void pickImage() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        startActivityForResult(chooserIntent, PICK_IMAGE_AVATAR);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_AVATAR && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mUri = data.getData();
            Glide.with(this).load(mUri).apply(RequestOptions.circleCropTransform()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    adapter.setPickedDrawable(resource);
                    return false;
                }
            }).into(mImage);
            picked = true;
        }
    }
}