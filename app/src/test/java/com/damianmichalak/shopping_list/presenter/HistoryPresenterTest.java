package com.damianmichalak.shopping_list.presenter;

import com.damianmichalak.shopping_list.Mocks;
import com.damianmichalak.shopping_list.helper.guava.Maps;
import com.damianmichalak.shopping_list.model.HistoryDao;
import com.damianmichalak.shopping_list.model.apiModels.Product;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import rx.Observable;
import rx.observers.TestSubscriber;

import static com.google.common.truth.Truth.assert_;
import static org.mockito.Mockito.when;

public class HistoryPresenterTest {


    private HistoryPresenter historyPresenter;

    @Mock
    private HistoryDao historyDao;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testWhenListIsEmptyEmitTrue() throws Exception {
        Map<String, Product> emptyList = Maps.newHashMap();
        when(historyDao.getProductsObservable()).thenReturn(Observable.just(emptyList));
        historyPresenter = new HistoryPresenter(historyDao);

        final TestSubscriber<Boolean> emptySubscriber = new TestSubscriber<>();
        historyPresenter.getHistoryEmptyObservable().subscribe(emptySubscriber);

        assert_().that(emptySubscriber.getOnNextEvents()).hasSize(1);
        assert_().that(emptySubscriber.getOnNextEvents().get(0)).isTrue();
    }

    @Test
    public void testWhenListIsEmptyEmitFalse() throws Exception {
        Map<String, Product> notEmptyList = Maps.newHashMap();
        notEmptyList.put("id1", Mocks.product());
        when(historyDao.getProductsObservable()).thenReturn(Observable.just(notEmptyList));
        historyPresenter = new HistoryPresenter(historyDao);

        final TestSubscriber<Boolean> emptySubscriber = new TestSubscriber<>();
        historyPresenter.getHistoryEmptyObservable().subscribe(emptySubscriber);

        assert_().that(emptySubscriber.getOnNextEvents()).hasSize(1);
        assert_().that(emptySubscriber.getOnNextEvents().get(0)).isFalse();
    }
}
