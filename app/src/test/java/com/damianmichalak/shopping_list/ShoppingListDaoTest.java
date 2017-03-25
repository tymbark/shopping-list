package com.damianmichalak.shopping_list;

import com.damianmichalak.shopping_list.helper.Database;
import com.damianmichalak.shopping_list.helper.EventsWrapper;
import com.damianmichalak.shopping_list.model.ShoppingList;
import com.damianmichalak.shopping_list.model.ShoppingListDao;
import com.damianmichalak.shopping_list.model.UserDao;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import static com.google.common.truth.Truth.assert_;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class ShoppingListDaoTest {

    @Mock
    private DatabaseReference reference;

    @Mock
    private DataSnapshot dataSnapshot;
    @Mock
    private DatabaseError databaseError;

    @Mock
    Database database;
    @Mock
    UserDao userDao;
    private PublishSubject<String> uidSubject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        uidSubject = PublishSubject.create();
        when(userDao.getUidObservable()).thenReturn(uidSubject);
        when(database.shoppingListReference()).thenReturn(reference);
    }

    @Test
    public void testPushingOneEvent_isSuccess() throws Exception {
        final EventsWrapper eventsWrapper = new EventsWrapper();
        final ShoppingListDao dao = new ShoppingListDao(database, eventsWrapper, new EventsWrapper(), userDao);
        final TestSubscriber<ShoppingList> subscriber = new TestSubscriber<>();

        dao.getShoppingListObservable().subscribe(subscriber);
        eventsWrapper.pushEventOnDataChange(dataSnapshot);

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
    }

    @Test
    public void testPushingOneEvent_isNotError() throws Exception {
        final EventsWrapper eventsWrapper = new EventsWrapper();
        final ShoppingListDao dao = new ShoppingListDao(database, eventsWrapper, new EventsWrapper(), userDao);
        final TestSubscriber<ShoppingList> subscriber = new TestSubscriber<>();

        dao.getShoppingListObservable().subscribe(subscriber);
        eventsWrapper.pushEventOnDataChange(dataSnapshot);

        assert_().that(subscriber.getOnErrorEvents()).hasSize(0);
    }

    @Test
    public void testPushingOneErrorEvent_isError() throws Exception {
        final EventsWrapper eventsWrapper = new EventsWrapper();
        final ShoppingListDao dao = new ShoppingListDao(database, eventsWrapper, new EventsWrapper(), userDao);
        final TestSubscriber<ShoppingList> subscriber = new TestSubscriber<>();

        dao.getShoppingListObservable().subscribe(subscriber);
        eventsWrapper.pushEventDatabaseError(databaseError);

        assert_().that(subscriber.getOnErrorEvents()).hasSize(1);
    }

    @Test
    public void testPushingOneErrorEvent_isNotSuccess() throws Exception {
        final EventsWrapper eventsWrapper = new EventsWrapper();
        final ShoppingListDao dao = new ShoppingListDao(database, eventsWrapper, new EventsWrapper(), userDao);
        final TestSubscriber<ShoppingList> subscriber = new TestSubscriber<>();

        dao.getShoppingListObservable().subscribe(subscriber);
        eventsWrapper.pushEventDatabaseError(databaseError);

        assert_().that(subscriber.getOnNextEvents()).hasSize(0);
    }

    @Test
    public void testPushingSomeValue_returnsThisValue() throws Exception {
        final EventsWrapper eventsWrapper = new EventsWrapper();
        final ShoppingListDao dao = new ShoppingListDao(database, eventsWrapper, new EventsWrapper(), userDao);
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
        final ShoppingListDao dao = new ShoppingListDao(database, eventsWrapper, new EventsWrapper(), userDao);
        final TestSubscriber<ShoppingList> subscriber1 = new TestSubscriber<>();
        final TestSubscriber<ShoppingList> subscriber2 = new TestSubscriber<>();

        dao.getShoppingListObservable().subscribe(subscriber1);
        dao.getShoppingListObservable().subscribe(subscriber2);

        when(dataSnapshot.getValue(any(Class.class))).thenReturn(new ShoppingList());
        eventsWrapper.pushEventOnDataChange(dataSnapshot);

    }

    @Test
    public void test() throws Exception {

        final EventsWrapper eventsWrapper = new EventsWrapper();
        final ShoppingListDao dao = new ShoppingListDao(database, new EventsWrapper(), eventsWrapper, userDao);
        final TestSubscriber<Map<String, String>> subscriber1 = new TestSubscriber<>();
        final TestSubscriber<Map<String, String>> subscriber2 = new TestSubscriber<>();

        dao.getProductsObservable().subscribe(subscriber1);
        eventsWrapper.pushEventOnDataChange(dataSnapshot);

        uidSubject.onNext("UID");
        dao.getProductsObservable().subscribe(subscriber2);
        eventsWrapper.pushEventOnDataChange(dataSnapshot);

        assert_().that(subscriber1.getOnNextEvents()).hasSize(1);
        assert_().that(subscriber2.getOnNextEvents()).hasSize(1);

    }

}