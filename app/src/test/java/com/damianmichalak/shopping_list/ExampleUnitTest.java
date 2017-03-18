package com.damianmichalak.shopping_list;

import com.damianmichalak.shopping_list.model.ShoppingList;
import com.damianmichalak.shopping_list.model.ShoppingListDao;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import rx.AsyncEmitter;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

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

        when(reference.addValueEventListener(testListner)).thenReturn(testListner);

        final List<ValueEventListener> listeners = new ArrayList<>();

        Observable.fromEmitter(new Action1<AsyncEmitter<ShoppingList>>() {
            @Override
            public void call(final AsyncEmitter<ShoppingList> asyncEmitter) {
                testListner = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ShoppingList shoppingList = dataSnapshot.getValue(ShoppingList.class);
                        asyncEmitter.onNext(shoppingList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        asyncEmitter.onError(new Throwable(databaseError.getMessage()));
                    }
                };
                listeners.add(testListner);
                reference.addValueEventListener(testListner);
            }
        }, AsyncEmitter.BackpressureMode.LATEST)
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        for (ValueEventListener listener : listeners) {
                            reference.removeEventListener(listener);
                        }
                        listeners.clear();

                    }
                });
    }


}