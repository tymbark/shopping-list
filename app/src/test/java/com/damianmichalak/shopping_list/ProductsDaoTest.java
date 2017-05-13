package com.damianmichalak.shopping_list;


import com.damianmichalak.shopping_list.helper.Database;
import com.damianmichalak.shopping_list.helper.EventsWrapper;
import com.damianmichalak.shopping_list.model.CurrentListDao;
import com.damianmichalak.shopping_list.model.ProductsDao;
import com.damianmichalak.shopping_list.model.UserDao;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.verify;
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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(currentListDao.getCurrentListKeyObservable()).thenReturn(Observable.just("ID"));
    }

    @Test
    public void testWhenAddingNewItem_databaseIsCalled() throws Exception {
        final EventsWrapper eventsWrapper = new EventsWrapper();
        final ProductsDao dao = new ProductsDao(database, eventsWrapper, currentListDao);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();

        dao.addNewItemObservable(Mocks.product()).subscribe(subscriber);

        verify(datab.push())
    }

}
