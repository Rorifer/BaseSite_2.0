package com.engineeringforyou.basesite.presentation.sitedetails;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.engineeringforyou.basesite.BuildConfig;
import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.models.Comment;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.models.Status;
import com.engineeringforyou.basesite.models.User;
import com.engineeringforyou.basesite.presentation.sitecreate.SiteCreateActivity;
import com.engineeringforyou.basesite.presentation.sitedetails.presenter.SiteDetailsPresenter;
import com.engineeringforyou.basesite.presentation.sitedetails.presenter.SiteDetailsPresenterImpl;
import com.engineeringforyou.basesite.presentation.sitedetails.views.CommentsAdapter;
import com.engineeringforyou.basesite.presentation.sitedetails.views.PhotoDetailsAdapter;
import com.engineeringforyou.basesite.presentation.sitedetails.views.SiteDetailsView;
import com.engineeringforyou.basesite.presentation.sitemap.MapActivity;
import com.engineeringforyou.basesite.presentation.streetview.StreetViewActivity;
import com.engineeringforyou.basesite.utils.EventFactory;
import com.engineeringforyou.basesite.utils.KeyBoardUtils;
import com.engineeringforyou.basesite.utils.ShareUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import uk.co.senab.photoview.PhotoView;

import static com.engineeringforyou.basesite.presentation.sitecreate.SiteCreateActivity.CODE_SITE_EDIT;
import static com.engineeringforyou.basesite.presentation.sitecreate.SiteCreateActivity.PHOTO_WIDTH;
import static com.engineeringforyou.basesite.presentation.sitecreate.SiteCreateActivity.SITE;

public class SiteDetailsActivity extends AppCompatActivity implements SiteDetailsView, PhotoDetailsAdapter.OnPhotoClickListener {

    public static final String KEY_SITE = "key_site";
    private static final String SHOW_PHOTO = "show_photo";

    @BindView(R.id.ad_mob_details)
    AdView mAdMobView;
    @BindView(R.id.progress_bar)
    FrameLayout mProgress;
    @BindView(R.id.scroll_view)
    ScrollView mScrollView;
    @BindView(R.id.site_number)
    AppCompatTextView siteNumber;
    @BindView(R.id.site_address)
    AppCompatTextView siteAddress;
    @BindView(R.id.site_object)
    AppCompatTextView siteObject;
    @BindView(R.id.site_coordinates)
    AppCompatTextView siteCoordinates;
    @BindView(R.id.site_status)
    AppCompatTextView siteStatus;
    @BindView(R.id.site_address_auto)
    AppCompatTextView siteAddressAuto;
    @BindView(R.id.address_auto_layout)
    LinearLayout addressAutoLayout;
    @BindView(R.id.comments_layout)
    LinearLayout commentsLayout;
    @BindView(R.id.comment_user_layout)
    LinearLayout commentUserLayout;
    @BindView(R.id.comment_user_text)
    EditText commentText;
    @BindView(R.id.comment_user_name)
    EditText userNameText;
    @BindView(R.id.comment_user_button)
    AppCompatButton commentButton;
    @BindView(R.id.comment_user_button_layout)
    LinearLayout commentButtonLayout;
    @BindView(R.id.comment_list)
    RecyclerView commentRecycler;
    @BindView(R.id.photo_list)
    RecyclerView photoRecycler;


    private SiteDetailsPresenter mPresenter;
    private Site mSite;
    private CommentsAdapter mAdapter;
    private PhotoDetailsAdapter mPhotoAdapter;
    private InterstitialAd mInterstitialAd;
    private boolean isShowPhoto;

    public static void start(Activity activity, Site site) {
        EventFactory.INSTANCE.openSiteDetails(site);
        startIntent(activity, site);
    }

    public static void startFromLink(Activity activity, Site site) {
        EventFactory.INSTANCE.openSiteDetailsFromLink(site);
        startIntent(activity, site);
    }

    private static void startIntent(Activity activity, Site site) {
        Intent intent = new Intent(activity, SiteDetailsActivity.class);
        intent.putExtra(KEY_SITE, site);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_details);
        ButterKnife.bind(this);
        mPresenter = new SiteDetailsPresenterImpl(this);
        mPresenter.bind(this);
        init();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean(SHOW_PHOTO, false)) {
            isShowPhoto = true;
            showProgress();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isShowPhoto) hideProgress();
        mAdMobView.resume();
    }

    private void init() {
        initAdMob();
        mSite = getIntent().getParcelableExtra(KEY_SITE);
        if (mSite != null) {
            initAdapter();
            setupView();

            commentText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    int oldState = commentButtonLayout.getVisibility();
                    commentButtonLayout.setVisibility(commentText.getText().toString().trim().isEmpty() ? View.GONE : View.VISIBLE);
                    int newState = commentButtonLayout.getVisibility();
                    if (oldState == View.GONE && newState == View.VISIBLE) {
                        mScrollView.post(() -> {
                            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            commentText.requestFocus();
                        });
                    }
                }
            });
        }
    }

    @SuppressLint("DefaultLocale")
    private void setupView() {
        mPresenter.setupName();
        //noinspection ConstantConditions
        siteNumber.setText(String.format("%s (%s)", mSite.getNumber(), mSite.getOperator().getLabel()));
        siteAddress.setText(mSite.getAddress());
        siteObject.setText(mSite.getObj());
        siteCoordinates.setText(String.format("%.6f° С.Ш.\n%.6f° В.Д.", mSite.getLatitude(), mSite.getLongitude()));
        siteStatus.setText(Status.values()[mSite.getStatusId() == null ? 0 : mSite.getStatusId()].getDescription());
        //noinspection ConstantConditions
        mPresenter.loadFields(mSite);
    }

    private void initAdapter() {
        mAdapter = new CommentsAdapter();
        commentRecycler.setLayoutManager(new LinearLayoutManager(this));
        commentRecycler.setAdapter(mAdapter);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int numberOfColumns = (int) (width / getResources().getDisplayMetrics().density) / PHOTO_WIDTH;
        photoRecycler.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        mPhotoAdapter = new PhotoDetailsAdapter(this, this);
        photoRecycler.setAdapter(mPhotoAdapter);
    }

    @Override
    public void setName(@NonNull String name) {
        userNameText.setText(name);
    }

    @Override
    public void showAdapter(@NotNull List<? extends Comment> list) {
        mAdapter.addList(list);
        commentsLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPhotos(@NotNull List<? extends Uri> list) {
        mPhotoAdapter.showPhotosFromUri(list);
    }

    @Override
    public void addUserComment(@NotNull Comment comment) {
        commentText.setText(null);
        mAdapter.addItem(comment);
        commentsLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void setAddressFromCoordinates(@NotNull String address) {
        addressAutoLayout.setVisibility(View.VISIBLE);
        siteAddressAuto.setText(address);
    }

    private void initAdMob() {
            initInterstitialAd();
            initAdMobV();
    }

    private void initAdMobV() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.admob_test_device))
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdMobView.loadAd(adRequest);
    }

    private void initInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                openRoute();
            }
        });
        String adUnitId = getString(BuildConfig.DEBUG ? R.string.interstitial_test_ad_unit_id : R.string.interstitial_ad_unit_id_1);
        mInterstitialAd.setAdUnitId(adUnitId);
//        loadInterstitialAd();
    }

    @Override
    public void loadInterstitialAd() {
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @OnClick(R.id.button_map)
    public void clickMapBtn() {
        showProgress();
        MapActivity.start(this, mSite);
    }

    @OnClick({R.id.button_edit, R.id.photo_button})
    public void clickMapEdit() {
        showProgress();
        SiteCreateActivity.startForEditSite(this, mSite);
    }

    @OnClick({R.id.button_share})
    public void clickShare() {
        ShareUtils.Companion.shareSite(this, mSite);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CODE_SITE_EDIT:
                if (resultCode == RESULT_OK && data != null) {
                    mSite = data.getParcelableExtra(SITE);
                    if (mSite == null) { // Непонятное падение  https://www.fabric.io/nouu/android/apps/com.engineeringforyou.basesite/issues/5b4990166007d59fcd7dc72f?time=last-ninety-days
                        EventFactory.INSTANCE.message("SiteDetailsActivity, mSite == null\n" +
                                data.getScheme() + "\n" +
                                data.getAction() + "\n" +
                                data.getType() + "\n" +
                                data.getDataString() + "\n" +
                                data.getPackage());
                    } else setupView();
                }
                break;
        }
    }

    @OnClick(R.id.button_route)
    public void clickRouteBtn() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            openRoute();
        }
    }

    private void openRoute() {
        EventFactory.INSTANCE.clickRouteBS(mSite);
        Double lat = mSite.getLatitude();
        Double lng = mSite.getLongitude();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("geo:%s,%s?q=%s,%s", lat, lng, lat, lng))));
        } catch (Exception e) {
            Toast.makeText(this, "Не удалось запустить навигатор", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.panorama_button)
    public void clickPanorama() {
        StreetViewActivity.Companion.start(this, mSite);
    }

    @OnClick(R.id.comment_user_button)
    public void clickCommentBtn() {
        KeyBoardUtils.hideKeyboard(this, getCurrentFocus());
        mPresenter.saveComment(mSite, commentText.getText().toString().trim(),
                new User(this, userNameText.getText().toString().trim()));
    }

    @OnEditorAction(R.id.comment_user_name)
    public boolean comment(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            clickCommentBtn();
            return true;
        }
        return false;
    }

    @Override
    public void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

    public void cancelShowPhoto() {
        hideProgress();
        isShowPhoto = false;
    }

    @Override
    public void showMessage(@NotNull String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SHOW_PHOTO, isShowPhoto);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        mAdMobView.pause();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> list = getSupportFragmentManager().getFragments();
        if (!list.isEmpty() && list.get(list.size() - 1) instanceof ImageFragment) {
            cancelShowPhoto();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mAdMobView.destroy();
        super.onDestroy();
        mPresenter.unbindView();
    }

    @Override
    public void onPhotoSelected(Uri uri) {
        showProgress();
        isShowPhoto = true;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, ImageFragment.newInstance(uri))
                .addToBackStack("TAG")
                .commit();
    }

    public static class ImageFragment extends Fragment {

        public static ImageFragment newInstance(Uri uri) {
            ImageFragment fragment = new ImageFragment();
            Bundle args = new Bundle();
            args.putParcelable("uri", uri);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_image, container, false);

            Uri mUri = getArguments().getParcelable("uri");
            PhotoView image = rootView.findViewById(R.id.image);
            image.setMaximumScale(10f);

            if (mUri != null) {
                Glide.with(this)
                        .load(mUri)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontTransform()
                        .into(image);
            }

            image.setOnLongClickListener(v -> {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                        ((SiteDetailsActivity) getActivity()).cancelShowPhoto();
                        return true;
                    }
            );
            return rootView;
        }
    }
}