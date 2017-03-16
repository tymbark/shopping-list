package com.damianmichalak.shopping_list.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.damianmichalak.shopping_list.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends BaseFragment {

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.string_save)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stringTest();
                    }
                });

        view.findViewById(R.id.obj_save)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        objectTest();
                    }
                });

        view.findViewById(R.id.user_test)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });


    }

    private void objectTest() {
        final FirebaseDatabase instance = FirebaseDatabase.getInstance();
        final DatabaseReference reference = instance.getReference("object");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("CHUJ", "data changed: " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reference.setValue(new Test());
    }

    private void stringTest() {
        final FirebaseDatabase instance = FirebaseDatabase.getInstance();
        final DatabaseReference reference = instance.getReference("test");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("CHUJ", "data changed: " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reference.setValue("Hello World1");
        reference.setValue("Hello World2");
    }

    @Override
    protected void inject() {

    }

    class Test {
        public String name = "Damian";
        public String surname = "M";
        public int age = 24;
        public long timestamp = System.currentTimeMillis();
        public List<String> contacts = new ArrayList<>();

        public Test() {
            contacts.add("Przemek");
            contacts.add("Kamil");
            contacts.add("Adam");
            contacts.add("Marek");
            contacts.add("Szymon");
        }
    }

}
