package com.damianmichalak.shopping_list.model;


import com.damianmichalak.shopping_list.helper.ProductsDatabase;
import com.damianmichalak.shopping_list.helper.guava.Maps;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
public class ExpListsDao {

    @Nonnull
    private final Map<String, ExpListDao> daos = Maps.newHashMap(); // todo cache this
    @Nonnull
    private final ProductsDatabase productsDatabase;

    public ExpListsDao(ProductsDatabase productsDatabase) {


        this.productsDatabase = productsDatabase;
    }

    public ExpListDao forId(String listId) {
        final ExpListDao expListDao = daos.get(listId);
        if (expListDao == null) {
            return daos.put(listId, new ExpListDao(listId, productsDatabase));
        } else {
            return expListDao;
        }
    }

    class ExpListDao {

        @Nonnull
        private final String listId;
        @Nonnull
        private final ProductsDatabase productsDatabase;

        ExpListDao(@Nonnull final String listId,
                @Nonnull final ProductsDatabase productsDatabase) {
            this.listId = listId;
            this.productsDatabase = productsDatabase;
        }


    }

}
