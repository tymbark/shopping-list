package com.damianmichalak.shopping_list;

import com.damianmichalak.shopping_list.helper.Database;
import com.damianmichalak.shopping_list.helper.EventsWrapper;
import com.damianmichalak.shopping_list.helper.guava.Lists;
import com.damianmichalak.shopping_list.model.ShoppingList;
import com.damianmichalak.shopping_list.model.ShoppingListDao;
import com.damianmichalak.shopping_list.model.UserDao;
import com.damianmichalak.shopping_list.model.UserPreferences;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import static com.google.common.truth.Truth.assert_;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class ShoppingListDaoTest {

    @Mock
    private DatabaseReference reference;
    @Mock
    private DataSnapshot dataSnapshot;
    @Mock
    private DatabaseError databaseError;
    @Mock
    private UserPreferences userPreferences;
    @Mock
    private UserDao userDao;
    @Mock
    private FirebaseDatabase firebaseDatabase;

    private Database database;
    private PublishSubject<String> uidSubject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        uidSubject = PublishSubject.create();
        when(userDao.getUidObservable()).thenReturn(uidSubject);
        when(firebaseDatabase.getReference(anyString())).thenReturn(reference);

        database = new Database(userPreferences, firebaseDatabase);

    }

    @Test
    public void testPushingOneEventShoppingList_isSuccess() throws Exception {
        final EventsWrapper eventsWrapper = new EventsWrapper();
        final ShoppingListDao dao = new ShoppingListDao(database, eventsWrapper, new EventsWrapper(), userDao);
        final TestSubscriber<ShoppingList> subscriber = new TestSubscriber<>();

        dao.getShoppingListObservable().subscribe(subscriber);
        eventsWrapper.pushEventOnDataChange(dataSnapshot);

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
    }

    @Test
    public void testPushingOneEventShoppingList_isNotError() throws Exception {
        final EventsWrapper eventsWrapper = new EventsWrapper();
        final ShoppingListDao dao = new ShoppingListDao(database, eventsWrapper, new EventsWrapper(), userDao);
        final TestSubscriber<ShoppingList> subscriber = new TestSubscriber<>();

        dao.getShoppingListObservable().subscribe(subscriber);
        eventsWrapper.pushEventOnDataChange(dataSnapshot);

        assert_().that(subscriber.getOnErrorEvents()).hasSize(0);
    }

    @Test
    public void testPushingOneErrorEventShoppingList_isError() throws Exception {
        final EventsWrapper eventsWrapper = new EventsWrapper();
        final ShoppingListDao dao = new ShoppingListDao(database, eventsWrapper, new EventsWrapper(), userDao);
        final TestSubscriber<ShoppingList> subscriber = new TestSubscriber<>();

        dao.getShoppingListObservable().subscribe(subscriber);
        eventsWrapper.pushEventDatabaseError(databaseError);

        assert_().that(subscriber.getOnErrorEvents()).hasSize(1);
    }

    @Test
    public void testPushingOneErrorEventShoppingList_isNotSuccess() throws Exception {
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
    public void testWhenUIDExistsAndValuePushed_isSuccess() throws Exception {
        final EventsWrapper eventsWrapper = new EventsWrapper();
        final ShoppingListDao dao = new ShoppingListDao(database, new EventsWrapper(), eventsWrapper, userDao);
        final TestSubscriber<Map<String, String>> subscriber1 = new TestSubscriber<>();

        when(dataSnapshot.getChildren()).thenReturn(Lists.newArrayList());

        dao.getProductsObservable().subscribe(subscriber1);

        uidSubject.onNext("UID");
        eventsWrapper.pushEventOnDataChange(dataSnapshot);

        assert_().that(subscriber1.getOnNextEvents()).hasSize(1);
    }

    @Test
    public void testWhenUIDNotExistsAndValuePushed_isEmpty() throws Exception {
        final EventsWrapper eventsWrapper = new EventsWrapper();
        final ShoppingListDao dao = new ShoppingListDao(database, new EventsWrapper(), eventsWrapper, userDao);
        final TestSubscriber<Map<String, String>> subscriber1 = new TestSubscriber<>();

        when(dataSnapshot.getChildren()).thenReturn(Lists.newArrayList());

        dao.getProductsObservable().subscribe(subscriber1);
        eventsWrapper.pushEventOnDataChange(dataSnapshot);

        assert_().that(subscriber1.getOnNextEvents()).isEmpty();
    }

}