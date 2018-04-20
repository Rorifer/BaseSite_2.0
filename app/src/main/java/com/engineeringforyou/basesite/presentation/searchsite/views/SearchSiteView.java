package com.engineeringforyou.basesite.presentation.searchsite.views;

import android.database.Cursor;
import android.support.annotation.StringRes;

public interface SearchSiteView {
    void setOperator(int operatorIndex);

    void showError(@StringRes int textRes);

    void showResult(@StringRes int textRes);

    void showProgress();

    void hideProgress();

    void toSiteInfo(Cursor cursor);

    void toSiteChoice(Cursor cursor, int count);
}
