package com.example.mytasker.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.mytasker.R;
import com.example.mytasker.activities.NotificationActivity;
import com.example.mytasker.activities.SettingActivity;
import com.example.mytasker.models.Profile;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.retrofit.NullOnEmptyConverterFactory;
import com.example.mytasker.util.Contracts;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.mytasker.util.Contracts.CODE_NOTIFICATION_ACTIVITY;
import static com.example.mytasker.util.Contracts.CODE_SETTINGS_ACTIVITY;

public class ProfileFragment extends Fragment {

    RatingBar taskerrating,posterrating;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.frag_profile, container, false);
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle("PROFILE");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        taskerrating =  v.findViewById(R.id.taskerrating);
        posterrating = v.findViewById(R.id.posterating);
        myapi(v);
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        if (item.getItemId() == R.id.setting) {
            intent = new Intent(getContext(), SettingActivity.class);
            getActivity().startActivityForResult(intent, CODE_SETTINGS_ACTIVITY);
//            getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            return true;
        }else {
            intent = new Intent(getContext(), NotificationActivity.class);
            getActivity().startActivityForResult(intent, CODE_NOTIFICATION_ACTIVITY);
//            getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            return true;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile_menu,menu);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public void myapi(View v)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Contracts.BASE_POST_URL)
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<Profile> call = jsonPlaceHolder.getProfile();
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {


                if (!response.isSuccessful()) {
                    Log.e("error",response.toString());
                    return;
                }
                taskerrating.setRating(response.body().getTasker_rating());
                posterrating.
                setupstats(response.body().getStats());
                setupskills(response.body().getSkills());
                setupmedals(response.body().getMedals());
                settupdetail(response.body().getDetail());


            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.e("error",t.getMessage());
            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
