package com.engineeringforyou.basesite.presentation.searchsite.presenter;

import com.engineeringforyou.basesite.presentation.searchsite.views.SearchSiteView;

public interface SearchSitePresenter {

    void bind(SearchSiteView view);

    void unbindView();

    void checkSavedOperator();

    void searchSite(int operatorIndex, String search);
}
