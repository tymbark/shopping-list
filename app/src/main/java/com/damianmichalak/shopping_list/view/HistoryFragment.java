package com.damianmichalak.shopping_list.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.model.ShoppingList;
import com.damianmichalak.shopping_list.model.User;
import com.damianmichalak.shopping_list.model.UserDao;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;

import rx.functions.Action1;

@Deprecated
public class HistoryFragment extends BaseFragment {

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Inject
    UserDao userDao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.add_list)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

        view.findViewById(R.id.obj_save)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

        view.findViewById(R.id.user_test)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

//        FirebaseDatabase.getInstance().getReference("shopping_lists/0").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                final ShoppingList shoppingList = dataSnapshot.getValue(ShoppingList.class);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


    }

    @Override
    protected void initDagger() {

    }

}
