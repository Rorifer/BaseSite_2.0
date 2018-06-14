package com.engineeringforyou.basesite.presentation.sitedetails.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.engineeringforyou.basesite.domain.sitedetails.SiteDetailsInteractor;
import com.engineeringforyou.basesite.domain.sitedetails.SiteDetailsInteractorImpl;
import com.engineeringforyou.basesite.models.Comment;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.presentation.sitedetails.views.SiteDetailsView;
import com.engineeringforyou.basesite.utils.EventFactory;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SiteDetailsPresenterImpl implements SiteDetailsPresenter {

    private CompositeDisposable mDisposable;
    private SiteDetailsView mView;
    private SiteDetailsInteractor mInteractor;

    public SiteDetailsPresenterImpl(Context context) {
        mDisposable = new CompositeDisposable();
        mInteractor = new SiteDetailsInteractorImpl(context);
    }

    @Override
    public void bind(@NonNull SiteDetailsView view) {
        mView = view;
    }

    @Override
    public void loadAddressFromCoordinates(double lat, double lng) {
        mDisposable.add(mInteractor.loadAddress(lat, lng)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::loadAddressSuccess, this::loadAddressError));
    }

    private void loadAddressSuccess(String address) {
        if (mView != null) mView.setAddressFromCoordinates(address);
    }

    private void loadAddressError(Throwable throwable) {
        EventFactory.INSTANCE.exception(throwable);
    }

    @Override
    public void showComments(@NotNull Site site) {
        mDisposable.add(mInteractor.getSavedComments(site)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::loadSavedCommentsSuccess, this::loadCommentsError));
    }

    @Override
    public void saveComment(@NotNull Site site, @NotNull String comment, @NotNull String user) {
        mDisposable.add(mInteractor.saveComment(new Comment(site, comment, user))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::saveCommentSuccess, this::saveCommentError));
    }

    private void saveCommentError(Throwable throwable) {

    }

    private void saveCommentSuccess() {

    }

    private void loadSavedCommentsSuccess(List<Comment> list) {
        if (mView != null) {
            mView.showAdapter(list);
            loadComments();
        }
    }

    public void loadComments() {
        mDisposable.add(mInteractor.loadComments()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::loadCommentsSuccess, this::loadCommentsError));
    }

    private void loadCommentsSuccess(List<Comment> list) {
        if (mView != null) {
            mView.showAdapter(list);
        }
    }

    private void loadCommentsError(Throwable throwable) {
        EventFactory.INSTANCE.exception(throwable);
    }

    @Override
    public void unbindView() {
        mDisposable.dispose();
        mView = null;
    }
}