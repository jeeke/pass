package com.example.mytasker.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.adapters.AdapterContacts;
import com.example.mytasker.models.DataGenerator;
import com.example.mytasker.models.People;
import com.example.mytasker.models.PeopleChip;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostFrag2 extends Fragment {
    private ChipsInput mChipsInput;
    public static EditText title,description;
    public static ArrayList<String> must_have = new ArrayList<>();
    private List<PeopleChip> items = new ArrayList<>();
    private List<ChipInterface> items_added = new ArrayList<>();
    private List<People> items_people = new ArrayList<>();
    public PostFrag2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.frag_post2, container, false);
        iniComponent(root);
        return root;
    }

    private void iniComponent(View view) {
        title = view.findViewById(R.id.post_title);
        description = view.findViewById(R.id.post_description);
        title.setActivated(false);
        title.setPressed(false);
        items_people = DataGenerator.getPeopleData(getContext());
        ((ImageButton) view.findViewById(R.id.contacts)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogContacts();
            }
        });

        mChipsInput = (ChipsInput) view.findViewById(R.id.chips_input);
        getPeopleChipList();
        mChipsInput.setActivated(false);
        // chips listener
        mChipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                must_have.add(chip.toString());
                items_added.add(chip);

            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                items_added.remove(chip);
            }

            @Override
            public void onTextChanged(CharSequence text) {
                //Log.e(TAG, "text changed: " + text.toString());
            }
        });
    }

    private void getPeopleChipList() {
        Integer id = 0;
        for (People p : items_people) {
            PeopleChip contactChip = new PeopleChip(id.toString(), p.imageDrw, p.name, p.email);
            // add contact to the list
            items.add(contactChip);
            id++;
        }
        // pass contact list to chips input
        mChipsInput.setFilterableList(items);
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_chips, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            view.finish();
//        } else {
//            Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void dialogContacts() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.skills_list);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        RecyclerView recyclerView =  dialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AdapterContacts _adapter = new AdapterContacts(getContext(), items_people);
        recyclerView.setAdapter(_adapter);
        _adapter.setOnItemClickListener(new AdapterContacts.OnItemClickListener() {
            @Override
            public void onItemClick(View view, People obj, int position) {
                mChipsInput.addChip(obj.imageDrw, obj.name, obj.email);
                dialog.hide();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

}
