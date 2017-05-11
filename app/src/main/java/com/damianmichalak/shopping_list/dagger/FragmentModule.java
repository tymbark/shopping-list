package com.damianmichalak.shopping_list.dagger;

import com.damianmichalak.shopping_list.view.BaseFragment;

import javax.annotation.Nonnull;

import dagger.Provides;

@dagger.Module
public class FragmentModule {

    @Nonnull
    private final BaseFragment fragment;

    public FragmentModule(BaseFragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    StringResources provideResources() {
        return id -> fragment.getResources().getString(id);
    }

}
