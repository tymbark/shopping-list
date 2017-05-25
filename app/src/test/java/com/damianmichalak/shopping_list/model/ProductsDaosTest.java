package com.damianmichalak.shopping_list.model;


import com.damianmichalak.shopping_list.Mocks;
import com.damianmichalak.shopping_list.helper.Database;
import com.damianmichalak.shopping_list.helper.References;
import com.damianmichalak.shopping_list.helper.guava.Maps;
import com.damianmichalak.shopping_list.model.api_models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.observers.TestSubscriber;

import static com.google.common.truth.Truth.assert_;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class ProductsDaosTest {

    @Mock
    private DataSnapshot dataSnapshot;
    @Mock
    private DatabaseError databaseError;
    @Mock
    private CurrentListDao currentListDao;
    @Mock
    private References references;
    @Mock
    private Database<Product> database;
    @Mock
    private HistoryDao historyDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(currentListDao.getCurrentListKeyObservable()).thenReturn(Observable.just("ID"));
    }

    @Test
    public void testWhenAddingNewItem_databaseIsCalled() throws Exception {
        when(database.put(any(Product.class), any(DatabaseReference.class))).thenReturn(Observable.just(true));
        final ProductsDao dao = new ProductsDao(references, database, currentListDao, historyDao);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();

        dao.addNewItemObservable(Mocks.product()).subscribe(subscriber);

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
        assert_().that(subscriber.getOnNextEvents().get(0)).isEqualTo(true);
    }

    @Test
    public void testWhenRemovingItem_databaseIsCalled() throws Exception {
        when(database.remove(anyString(), any(DatabaseReference.class))).thenReturn(Observable.just(true));
        when(historyDao.addNewItemObservable(any(Product.class))).thenReturn(Observable.just(true));
        final ProductsDao dao = new ProductsDao(references, database, currentListDao, historyDao);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();

        dao.removeItemByKeyObservable(Mocks.product()).subscribe(subscriber);

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
        assert_().that(subscriber.getOnNextEvents().get(0)).isEqualTo(true);
    }


    @Test
    public void testWhenGettingItems_databaseIsCalled() throws Exception {
        when(database.itemsAsMap(any(DatabaseReference.class), eq(Product.class))).thenReturn(Observable.just(Maps.newHashMap()));
        final ProductsDao dao = new ProductsDao(references, database, currentListDao, historyDao);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();

        dao.getProductsObservable().subscribe(subscriber);

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
    }

}
