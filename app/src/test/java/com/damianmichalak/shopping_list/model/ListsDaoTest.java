package com.damianmichalak.shopping_list.model;

import com.damianmichalak.shopping_list.helper.Database;
import com.damianmichalak.shopping_list.helper.References;
import com.damianmichalak.shopping_list.helper.guava.Maps;
import com.damianmichalak.shopping_list.model.api_models.ShoppingList;
import com.google.firebase.database.DatabaseReference;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import rx.Observable;
import rx.observers.TestSubscriber;

import static com.google.common.truth.Truth.assert_;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class ListsDaoTest {

    private ListsDao listsDao;
    @Mock
    private References references;
    @Mock
    private Database<ShoppingList> shoppingListDb;
    @Mock
    private Database<String> listsIdsDb;
    @Mock
    private CurrentListDao currentListDao;
    @Mock
    private UserDao userDao;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testThatDaoReturnsItemsFromDB() throws Exception {
        when(userDao.getUidObservable()).thenReturn(Observable.just("uid"));
        Map<String, String> items = Maps.newHashMap();
        items.put("id1", "list1");
        items.put("id2", "list2");
        items.put("id3", "list3");
        when(listsIdsDb.itemsAsMap(any(DatabaseReference.class), any())).thenReturn(Observable.just(items));
        listsDao = new ListsDao(references, shoppingListDb, listsIdsDb, currentListDao, userDao);

        final TestSubscriber<Map<String, String>> subscriber = new TestSubscriber<>();
        listsDao.getAvailableListsObservable().subscribe(subscriber);

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
        assert_().that(subscriber.getOnNextEvents().get(0)).hasSize(3);
    }

    @Test
    public void testWhenAddingFirstList_setAsCurrentList() throws Exception {
        Map<String, String> items = Maps.newHashMap();
        items.put("id1", "list1");
        final TestSubscriber<String> setCurrentListSubscriber = new TestSubscriber<>();
        when(userDao.getUidObservable()).thenReturn(Observable.just("uid"));
        when(listsIdsDb.itemsAsMap(any(DatabaseReference.class), any())).thenReturn(Observable.just(items));
        when(currentListDao.saveCurrentListIdObserver()).thenReturn(setCurrentListSubscriber);
        listsDao = new ListsDao(references, shoppingListDb, listsIdsDb, currentListDao, userDao);

        listsDao.getAvailableListsObservable().subscribe();

        assert_().that(setCurrentListSubscriber.getOnNextEvents()).hasSize(1);
        assert_().that(setCurrentListSubscriber.getOnNextEvents().get(0)).isEqualTo("id1");
    }

}
