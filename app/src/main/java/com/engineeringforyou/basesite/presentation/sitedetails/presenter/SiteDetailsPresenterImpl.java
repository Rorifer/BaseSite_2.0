package com.engineeringforyou.basesite.presentation.sitedetails.presenter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.engineeringforyou.basesite.domain.sitedetails.SiteDetailsInteractor;
import com.engineeringforyou.basesite.domain.sitedetails.SiteDetailsInteractorImpl;
import com.engineeringforyou.basesite.models.Comment;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.models.User;
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
    private Comment mComment;
    private Site mSite;

    public SiteDetailsPresenterImpl(Context context) {
        mDisposable = new CompositeDisposable();
        mInteractor = new SiteDetailsInteractorImpl(context);
    }

    @Override
    public void bind(@NonNull SiteDetailsView view) {
        mView = view;
    }

    @Override
    public void setupName() {
        mView.setName(mInteractor.getName());
    }

    private void loadAddressFromCoordinates() {
        Double lat = mSite.getLatitude();
        Double lng = mSite.getLongitude();
        if (lat == null || lng == null) return;
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
    public void loadFields(@NotNull Site site) {
        mSite = site;
        loadAddressFromCoordinates();
        loadCommentsAndThenLoadPhotos();
    }

    private void loadPhotos() {
        mDisposable.clear();
        mDisposable.add(mInteractor.loadPhotos(mSite)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::loadPhotosSuccess, this::loadPhotosError));
    }

    private void loadPhotosSuccess(List<Uri> uriList) {
        if (mView != null) {
            mView.showPhotos(uriList);
        }
    }

    private void loadPhotosError(Throwable throwable) {
        EventFactory.INSTANCE.exception(throwable);
    }

    private void loadCommentsAndThenLoadPhotos() {
        mDisposable.add(mInteractor.loadComments(mSite)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::loadSavedCommentsSuccess, this::loadCommentsError));
    }

    @Override
    public void saveComment(@NotNull Site site, @NotNull String text, @NotNull User user) {
        if (!text.isEmpty()) {
            mView.showProgress();
            mInteractor.saveName(user.getUserName());
            mComment = new Comment(site, text, user);
            mDisposable.add(mInteractor.saveComment(mComment)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((this::saveCommentSuccess), (this::saveCommentError)));
        }
    }

    private void saveCommentError(Throwable throwable) {
        EventFactory.INSTANCE.exception(throwable);
        if (mView != null) {
            mView.hideProgress();
            mView.showMessage("Сохранение комментария не удалось. Проверьте интернет-соединение");
        }
    }

    private void saveCommentSuccess() {
        if (mView != null) {
            mView.addUserComment(mComment);
            mView.hideProgress();
            mView.showMessage("Сохранение комментария выполнено");
        }
    }

    private void loadSavedCommentsSuccess(List<Comment> list) {
        if (mView != null && !list.isEmpty()) {
            mView.showAdapter(list);
            loadPhotos();
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