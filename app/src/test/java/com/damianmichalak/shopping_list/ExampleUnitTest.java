package com.damianmichalak.shopping_list;

import com.damianmichalak.shopping_list.model.ShoppingListDao;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class ExampleUnitTest {

    @Mock
    private DatabaseReference reference;
    private com.google.firebase.database.ValueEventListener testListner;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);


    }

    @Test
    public void addition_isCorrect() throws Exception {

        testListner = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        when(reference.addValueEventListener(any(ValueEventListener.class))).thenReturn(testListner);


        ShoppingListDao dao = new ShoppingListDao(reference);
        dao.getListObservable().subscribe();
    }


}