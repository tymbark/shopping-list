package com.damianmichalak.shopping_list;


import com.damianmichalak.shopping_list.helper.Database;
import com.damianmichalak.shopping_list.helper.ProductsDatabase;
import com.damianmichalak.shopping_list.helper.guava.Maps;
import com.damianmichalak.shopping_list.model.CurrentListDao;
import com.damianmichalak.shopping_list.model.ProductsDao;
import com.damianmichalak.shopping_list.model.api_models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.observers.TestSubscriber;

import static com.google.common.truth.Truth.assert_;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class ProductsDaoTest {

    @Mock
    private DataSnapshot dataSnapshot;
    @Mock
    private DatabaseError databaseError;
    @Mock
    private CurrentListDao currentListDao;
    @Mock
    private Database database;
    @Mock
    private ProductsDatabase productsDatabase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(currentListDao.getCurrentListKeyObservable()).thenReturn(Observable.just("ID"));
    }

    @Test
    public void testWhenAddingNewItem_databaseIsCalled() throws Exception {
        when(productsDatabase.put(any(Product.class), anyString())).thenReturn(Observable.just(true));
        final ProductsDao dao = new ProductsDao(productsDatabase, currentListDao);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();

        dao.addNewItemObservable(Mocks.product()).subscribe(subscriber);

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
        assert_().that(subscriber.getOnNextEvents().get(0)).isEqualTo(true);
    }

    @Test
    public void testWhenRemovingItem_databaseIsCalled() throws Exception {
        when(productsDatabase.remove(anyString(), anyString())).thenReturn(Observable.just(true));
        final ProductsDao dao = new ProductsDao(productsDatabase, currentListDao);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();

        dao.removeItemByKeyObservable("product_ID").subscribe(subscriber);

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
        assert_().that(subscriber.getOnNextEvents().get(0)).isEqualTo(true);
    }


    @Test
    public void testWhenGettingItems_databaseIsCalled() throws Exception {
        when(productsDatabase.products(anyString())).thenReturn(Observable.just(Maps.newHashMap()));
        final ProductsDao dao = new ProductsDao(productsDatabase, currentListDao);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();

        dao.getProductsObservable().subscribe(subscriber);

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
    }

}
