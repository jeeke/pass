package com.esselion.pass.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.esselion.pass.R;
import com.esselion.pass.Server;
import com.esselion.pass.adapters.AvatarAdapter;
import com.esselion.pass.util.Contracts;
import com.esselion.pass.util.Tools;

import static com.esselion.pass.util.Cache.getUser;
import static com.esselion.pass.util.Tools.launchActivityForResult;
import static com.esselion.pass.util.Tools.showSnackBar;

public class AvatarChooser extends BaseActivity implements AvatarAdapter.RecyclerViewClickListener {

    public static final int PICK_IMAGE_AVATAR = 128;
    RecyclerView mRecyclerView;
    boolean prevCallResolved = true;
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
        Tools.initMinToolbar(this, "EDIT IMAGE");
    }

    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.update_image).setOnClickListener(v -> {
            if (!prevCallResolved || server == null)
                showSnackBar(this, "Error, Please try later");
            else if (pickedPosition == -1)
                showSnackBar(this, "Please pick an image");
            else if (pickedPosition != 0) {
                server.updateImage(getUser(), Contracts.avatars[pickedPosition - 1], null, null);
                prevCallResolved = false;
            } else {
                server.updateImage(getUser(), null, mUri, mImage);
                prevCallResolved = false;
            }
        });
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
        launchActivityForResult(this, chooserIntent, PICK_IMAGE_AVATAR);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onServerCallSuccess(int methodId, String title) {
        super.onServerCallSuccess(methodId, title);
        if (methodId == Server.SERVER_UPDATE_IMAGE) finish();
    }
}