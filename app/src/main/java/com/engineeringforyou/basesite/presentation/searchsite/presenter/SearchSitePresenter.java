package com.engineeringforyou.basesite.presentation.searchsite.presenter;

import android.widget.EditText;

import com.engineeringforyou.basesite.presentation.searchsite.views.SearchSiteView;

public interface SearchSitePresenter {

    void bind(SearchSiteView view);

    void unbindView();

    void setupOperator();

    void searchSite(String search);

    void watchChanges(EditText view);

    void saveOperator(int operatorIndex);
}
