package com.damianmichalak.shopping_list.presenter;

import com.damianmichalak.shopping_list.helper.guava.Maps;
import com.damianmichalak.shopping_list.model.CurrentListDao;
import com.damianmichalak.shopping_list.model.ListsDao;
import com.jacekmarchwicki.universaladapter.BaseAdapterItem;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import static com.google.common.truth.Truth.assert_;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
public class ShoppingListPresenterTest {

    ShoppingListPresenter presenter;

    @Mock
    CurrentListDao currentListDao;
    @Mock
    ListsDao listsDao;

    PublishSubject addEmptyClickObservable = PublishSubject.create();
    PublishSubject addListClickObservable = PublishSubject.create();


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testThatPresenterEmitsItemsFromDao() throws Exception {
        final HashMap<String, String> items = Maps.newHashMap();
        items.put("key1", "val1");
        when(listsDao.getAvailableListsObservable()).thenReturn(Observable.just(items));
        when(listsDao.removeListObservable(anyString())).thenReturn(Observable.never());
        when(listsDao.addNewAvailableListObservable(anyString(), anyString())).thenReturn(Observable.never());
        when(currentListDao.saveCurrentListIdObserver()).thenReturn(PublishSubject.create());
        presenter = new ShoppingListPresenter(currentListDao, listsDao, addEmptyClickObservable, addListClickObservable);

        final TestSubscriber<List<BaseAdapterItem>> subscriber = new TestSubscriber<>();
        presenter.getListObservable().subscribe(subscriber);

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
    }

    @Test
    public void testWhenLongClickedOnItemThatIsNotCurrentList_allowRemoveThisItem() throws Exception {
        final HashMap<String, String> items = Maps.newHashMap();
        items.put("key1", "val1");
        when(listsDao.getAvailableListsObservable()).thenReturn(Observable.just(items));
        when(listsDao.removeListObservable(anyString())).thenReturn(Observable.never());
        when(listsDao.addNewAvailableListObservable(anyString(), anyString())).thenReturn(Observable.never());
        when(currentListDao.saveCurrentListIdObserver()).thenReturn(PublishSubject.create());
        when(currentListDao.getCurrentListKeyObservable()).thenReturn(Observable.just("current list"));
        presenter = new ShoppingListPresenter(currentListDao, listsDao, addEmptyClickObservable, addListClickObservable);

        final TestSubscriber<List<BaseAdapterItem>> subscriber = new TestSubscriber<>();
        presenter.getListObservable().subscribe(subscriber);

        final ShoppingListPresenter.ShoppingListItem adapterItem = (ShoppingListPresenter.ShoppingListItem) subscriber.getOnNextEvents().get(0).get(0);

        final TestSubscriber<Boolean> adapterItemSubscriber = new TestSubscriber<>();
        adapterItem.removeAllowedObservable().subscribe(adapterItemSubscriber);

        // perform click:
        adapterItem.longClickObserver().onNext(null);

        assert_().that(adapterItemSubscriber.getOnNextEvents()).hasSize(1);
        assert_().that(adapterItemSubscriber.getOnNextEvents().get(0)).isTrue();
    }

    @Test
    public void testWhenLongClickedOnItemThatIsCurrentList_allowRemoveThisItem() throws Exception {
        final HashMap<String, String> items = Maps.newHashMap();
        items.put("current list", "val1");
        when(listsDao.getAvailableListsObservable()).thenReturn(Observable.just(items));
        when(listsDao.removeListObservable(anyString())).thenReturn(Observable.never());
        when(listsDao.addNewAvailableListObservable(anyString(), anyString())).thenReturn(Observable.never());
        when(currentListDao.saveCurrentListIdObserver()).thenReturn(PublishSubject.create());
        when(currentListDao.getCurrentListKeyObservable()).thenReturn(Observable.just("current list"));
        presenter = new ShoppingListPresenter(currentListDao, listsDao, addEmptyClickObservable, addListClickObservable);

        final TestSubscriber<List<BaseAdapterItem>> subscriber = new TestSubscriber<>();
        presenter.getListObservable().subscribe(subscriber);

        final ShoppingListPresenter.ShoppingListItem adapterItem = (ShoppingListPresenter.ShoppingListItem) subscriber.getOnNextEvents().get(0).get(0);

        final TestSubscriber<Boolean> adapterItemSubscriber = new TestSubscriber<>();
        adapterItem.removeAllowedObservable().subscribe(adapterItemSubscriber);

        // perform click:
        adapterItem.longClickObserver().onNext(null);

        assert_().that(adapterItemSubscriber.getOnNextEvents()).hasSize(1);
        assert_().that(adapterItemSubscriber.getOnNextEvents().get(0)).isFalse();
    }

    @Test
    public void testWhenItemIsClicked_setNewCurrentList() throws Exception {
        final HashMap<String, String> items = Maps.newHashMap();
        final TestSubscriber<String> newListSubscriber = new TestSubscriber<>();
        items.put("key1", "val1");
        when(listsDao.getAvailableListsObservable()).thenReturn(Observable.just(items));
        when(listsDao.removeListObservable(anyString())).thenReturn(Observable.never());
        when(listsDao.addNewAvailableListObservable(anyString(), anyString())).thenReturn(Observable.never());
        when(currentListDao.saveCurrentListIdObserver()).thenReturn(newListSubscriber);
        when(currentListDao.getCurrentListKeyObservable()).thenReturn(Observable.just("current list"));
        presenter = new ShoppingListPresenter(currentListDao, listsDao, addEmptyClickObservable, addListClickObservable);

        final TestSubscriber<List<BaseAdapterItem>> subscriber = new TestSubscriber<>();
        presenter.getListObservable().subscribe(subscriber);

        final ShoppingListPresenter.ShoppingListItem adapterItem = (ShoppingListPresenter.ShoppingListItem) subscriber.getOnNextEvents().get(0).get(0);

        // perform click:
        adapterItem.clickObserver().onNext(null);

        assert_().that(newListSubscriber.getOnNextEvents()).hasSize(1);
        assert_().that(newListSubscriber.getOnNextEvents().get(0)).contains("key1");
    }

}
