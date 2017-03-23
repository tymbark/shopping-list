package com.damianmichalak.shopping_list;

import com.damianmichalak.shopping_list.helper.EventsWrapper;
import com.damianmichalak.shopping_list.model.ShoppingList;
import com.damianmichalak.shopping_list.model.ShoppingListDao;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
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
import rx.observers.TestSubscriber;

import static com.google.common.truth.Truth.assert_;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class ExampleUnitTest {

    @Mock
    private DatabaseReference reference;
    private com.google.firebase.database.ValueEventListener testListner;

    @Mock
    private DataSnapshot dataSnapshot;
    @Mock
    private DatabaseError databaseError;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPushingOneEvent_isSuccess() throws Exception {
        final EventsWrapper eventsWrapper = new EventsWrapper();
        final ShoppingListDao dao = new ShoppingListDao(reference, eventsWrapper);
        final TestSubscriber<ShoppingList> subscriber = new TestSubscriber<>();

        dao.getShoppingListObservable().subscribe(subscriber);
        eventsWrapper.pushEventOnDataChange(dataSnapshot);

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
    }

    @Test
    public void testPushingOneEvent_isNotError() throws Exception {
        final EventsWrapper eventsWrapper = new EventsWrapper();
        final ShoppingListDao dao = new ShoppingListDao(reference, eventsWrapper);
        final TestSubscriber<ShoppingList> subscriber = new TestSubscriber<>();

        dao.getShoppingListObservable().subscribe(subscriber);
        eventsWrapper.pushEventOnDataChange(dataSnapshot);

        assert_().that(subscriber.getOnErrorEvents()).hasSize(0);
    }

    @Test
    public void testPushingOneErrorEvent_isError() throws Exception {
        final EventsWrapper eventsWrapper = new EventsWrapper();
        final ShoppingListDao dao = new ShoppingListDao(reference, eventsWrapper);
        final TestSubscriber<ShoppingList> subscriber = new TestSubscriber<>();

        dao.getShoppingListObservable().subscribe(subscriber);
        eventsWrapper.pushEventDatabaseError(databaseError);

        assert_().that(subscriber.getOnErrorEvents()).hasSize(1);
    }

    @Test
    public void testPushingOneErrorEvent_isNotSuccess() throws Exception {
        final EventsWrapper eventsWrapper = new EventsWrapper();
        final ShoppingListDao dao = new ShoppingListDao(reference, eventsWrapper);
        final TestSubscriber<ShoppingList> subscriber = new TestSubscriber<>();

        dao.getShoppingListObservable().subscribe(subscriber);
        eventsWrapper.pushEventDatabaseError(databaseError);

        assert_().that(subscriber.getOnNextEvents()).hasSize(0);
    }

    @Test
    public void testPushingSomeValue_returnsThisValue() throws Exception {
        final EventsWrapper eventsWrapper = new EventsWrapper();
        final ShoppingListDao dao = new ShoppingListDao(reference, eventsWrapper);
        final TestSubscriber<ShoppingList> subscriber = new TestSubscriber<>();

        final ShoppingList value = new ShoppingList();
        when(dataSnapshot.getValue(any(Class.class))).thenReturn(value);

        dao.getShoppingListObservable().subscribe(subscriber);
        eventsWrapper.pushEventOnDataChange(dataSnapshot);

        assert_().that(subscriber.getOnNextEvents().get(0)).isEqualTo(value);
    }

    @Test
    public void x() throws Exception {
        final EventsWrapper eventsWrapper = new EventsWrapper();
        final ShoppingListDao dao = new ShoppingListDao(reference, eventsWrapper);
        final TestSubscriber<ShoppingList> subscriber1 = new TestSubscriber<>();
        final TestSubscriber<ShoppingList> subscriber2 = new TestSubscriber<>();

        dao.getShoppingListObservable().subscribe(subscriber1);
        dao.getShoppingListObservable().subscribe(subscriber2);

        when(dataSnapshot.getValue(any(Class.class))).thenReturn(new ShoppingList());
        eventsWrapper.pushEventOnDataChange(dataSnapshot);

    }

}