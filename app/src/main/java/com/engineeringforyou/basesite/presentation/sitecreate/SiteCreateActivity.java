package com.engineeringforyou.basesite.presentation.sitecreate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.models.Operator;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.models.Status;
import com.engineeringforyou.basesite.presentation.mapcoordinates.MapCoordinatesActivity;
import com.engineeringforyou.basesite.presentation.sitecreate.presenter.SiteCreatePresenter;
import com.engineeringforyou.basesite.presentation.sitecreate.presenter.SiteCreatePresenterImpl;
import com.engineeringforyou.basesite.presentation.sitecreate.views.PhotoAdapter;
import com.engineeringforyou.basesite.presentation.sitecreate.views.SiteCreateView;
import com.engineeringforyou.basesite.utils.Utils;
import com.google.android.gms.maps.model.CameraPosition;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.engineeringforyou.basesite.presentation.mapcoordinates.MapCoordinatesActivity.CODE_MAP;
import static com.engineeringforyou.basesite.presentation.mapcoordinates.MapCoordinatesActivity.LATITUDE;
import static com.engineeringforyou.basesite.presentation.mapcoordinates.MapCoordinatesActivity.LONGITUDE;
import static com.engineeringforyou.basesite.presentation.sitemap.MapActivity.BORDER_LAT_END;
import static com.engineeringforyou.basesite.presentation.sitemap.MapActivity.BORDER_LAT_START;
import static com.engineeringforyou.basesite.presentation.sitemap.MapActivity.BORDER_LNG_END;
import static com.engineeringforyou.basesite.presentation.sitemap.MapActivity.BORDER_LNG_START;

public class SiteCreateActivity extends AppCompatActivity implements SiteCreateView {

    private static final String POSITION = "position";
    public static final String SITE = "site";
    public static final int CODE_SITE_CREATE = 365;
    public static final int CODE_SITE_EDIT = 366;
    private static final int CODE_PHOTO = 477;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int MAX_COUNT_PHOTO = 3;
    public static final int PHOTO_WIDTH = 166;

    @BindView(R.id.site_operator_spinner)
    AppCompatSpinner mOperatorSpinner;
    @BindView(R.id.site_status_spinner)
    AppCompatSpinner mStatusSpinner;
    @BindView(R.id.layout_site_status_spinner)
    View mStatusSpinnerLayout;
    @BindView(R.id.site_number)
    EditText mNumber;
    @BindView(R.id.coordinates_lat)
    EditText mLat;
    @BindView(R.id.coordinates_long)
    EditText mLong;
    @BindView(R.id.site_address)
    EditText mAddress;
    @BindView(R.id.site_object)
    EditText mObject;
    @BindView(R.id.site_name_user)
    EditText mUserName;
    @BindView(R.id.progress_bar)
    View mProgress;
    @BindView(R.id.save_site_draft_button)
    Button mButton;
    @BindView(R.id.photo_button)
    AppCompatImageButton mPhotoButton;
    @BindView(R.id.photo_recycler_view)
    RecyclerView mPhotoRecyclerView;

    private SiteCreatePresenter mPresenter;
    private Site mSite;
    private String mAddressLoaded = null;
    private PhotoAdapter mPhotoAdapter;

    public static void startForCreateSite(Activity activity, @Nullable CameraPosition position) {
        Intent intent = new Intent(activity, SiteCreateActivity.class);
        intent.putExtra(POSITION, position);
        activity.startActivityForResult(intent, CODE_SITE_CREATE);
        activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out);
    }

    public static void startForEditSite(Activity activity, Site site) {
        Intent intent = new Intent(activity, SiteCreateActivity.class);
        intent.putExtra(SITE, site);
        activity.startActivityForResult(intent, CODE_SITE_EDIT);
        activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_create);
        ButterKnife.bind(this);
        mPresenter = new SiteCreatePresenterImpl(this);
        mPresenter.bind(this);
        mPresenter.setupView();
        initToolbar();
        initSpinners();
        mSite = getIntent().getParcelableExtra(SITE);
        if (mSite != null) setupViewForEdit();
        setupRecyclerPhoto();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupEnablePhotoButton();
    }

    private void setupRecyclerPhoto() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int numberOfColumns = (int) (width / getResources().getDisplayMetrics().density) / PHOTO_WIDTH;
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        mPhotoAdapter = new PhotoAdapter();
        mPhotoRecyclerView.setAdapter(mPhotoAdapter);
    }

    private void setupViewForEdit() {
        setTitle(R.string.edit_bs);
        mNumber.setText(mSite.getNumber());
        mLat.setText(String.valueOf(mSite.getLatitude()));
        mLong.setText(String.valueOf(mSite.getLongitude()));
        mAddress.setText(mSite.getAddress());
        mObject.setText(mSite.getObj());
        mOperatorSpinner.setSelection(Objects.requireNonNull(mSite.getOperator()).ordinal() + 1);
        mButton.setText(R.string.edit);
        mOperatorSpinner.setEnabled(false);
        mSite.setLatitude(Double.parseDouble(mLat.getText().toString()));
        mSite.setLongitude(Double.parseDouble(mLong.getText().toString()));
        mStatusSpinnerLayout.setVisibility(View.VISIBLE);
        mStatusSpinner.setSelection(mSite.getStatusId() == null ? 0 : mSite.getStatusId());
    }

    private void initToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initSpinners() {
        String[] operators = {"Выберите оператора", Operator.MTS.getLabel(), Operator.MEGAFON.getLabel(),
                Operator.VIMPELCOM.getLabel(), Operator.TELE2.getLabel()};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, operators);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mOperatorSpinner.setAdapter(adapter);
        mOperatorSpinner.setPrompt("Операторы");
        mOperatorSpinner.setSelection(-1);

        String[] statuses = {Status.ACTIVE.getDescription(), Status.DISMANTLED.getDescription(), Status.NOT_EXIST.getDescription()};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStatusSpinner.setAdapter(statusAdapter);
        mOperatorSpinner.setPrompt("Статусы");
//        mStatusSpinner.setSelection(0);
    }

    @OnClick(R.id.save_site_draft_button)
    public void saveDraft() {
        if (mOperatorSpinner.getSelectedItemPosition() < 1) {
            showMessage(R.string.select_operator);
            return;
        }

        Double lat = getLatitude();
        Double lng = getLongitude();

        if (lat == null || lng == null) {
            showMessage(R.string.error_site_coordinates);
            return;
        }

        if (lat > BORDER_LAT_END || lat < BORDER_LAT_START
                || lng > BORDER_LNG_END || lng < BORDER_LNG_START) {
            showMessage(R.string.error_site_coordinates_border);
            return;
        }

        Site site = getCreatedSite();
        if (mSite != null) mPresenter.editSite(mSite, site, mPhotoAdapter.getUriList() , mUserName.getText().toString() );
        else mPresenter.saveSite(site, mPhotoAdapter.getUriList(), mUserName.getText().toString());
    }

    private Site getCreatedSite() {
        if (mOperatorSpinner.getSelectedItemPosition() < 1) {
            showMessage(R.string.select_operator);
            return null;
        }
        String number = mNumber.getText().toString().trim();
        Long timestamp = new Date().getTime();
        return new Site(
                null,
                Operator.values()[mOperatorSpinner.getSelectedItemPosition() - 1],
                number.isEmpty() ? "неизвестен" : number,
                getLatitude(),
                getLongitude(),
                mAddress.getText().toString().trim(),
                mObject.getText().toString().trim(),
                mSite == null ? (number.isEmpty() ? "unknown" : number).concat("_").concat(String.valueOf(timestamp)) :
                        mSite.getUid() == null ? mSite.getNumber() : mSite.getUid(),
                mSite == null ? Status.ACTIVE.ordinal() : mStatusSpinner.getSelectedItemPosition(),
                timestamp,
                Utils.INSTANCE.getAndroidId(this)
        );
    }

    private Double getLongitude() {
        try {
            return Double.parseDouble(mLong.getText().toString().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double getLatitude() {
        try {
            return Double.parseDouble(mLat.getText().toString().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void setName(@NonNull String name) {
        mUserName.setText(name);
    }

    @OnClick(R.id.coordinates_button)
    public void clickMap() {
        MapCoordinatesActivity.startForResult(this, getLatitude(), getLongitude(), getIntent().getParcelableExtra(POSITION));
    }

    @OnClick(R.id.photo_button)
    public void clickPhoto() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
//        } else {
        startPhoto(true);
//        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startPhoto(true);
//            } else {
//                startPhoto(false);
//            }
//        }
//    }

    private void startPhoto(boolean isCameraGranted) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
        chooser.putExtra(Intent.EXTRA_TITLE, getString(R.string.chose_photo_source));

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (isCameraGranted && cameraIntent.resolveActivity(getPackageManager()) != null) {
            Intent[] intentArray = {cameraIntent};
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        }
        startActivityForResult(chooser, CODE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_MAP) {
            if (resultCode == RESULT_OK) {
                Double lat = data.getDoubleExtra(LATITUDE, 0);
                Double lng = data.getDoubleExtra(LONGITUDE, 0);
                if (lat != 0 && lng != 0) {
                    mLat.setText(String.valueOf(lat));
                    mLong.setText(String.valueOf(lng));
                    mPresenter.loadAddressFromCoordinates(lat, lng);
                }
            }
        } else if (requestCode == CODE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
//                Bitmap imageBitmap = (Bitmap) extras.get("data"); // Загрузка из Камеры превью фотографии
//                showImage(imageBitmap);
            } else {
                Uri extrasUri = data.getData();
                if (extrasUri != null) { // Загрузка из Галереи
                    showImage(extrasUri);
//                    showImageUri(data.getData());
//                    try {
////                        mImage.setImageURI(mUri);
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), extrasUri);
//                        showImage(bitmap);
//                    } catch (IOException e) {
//                        EventFactory.INSTANCE.exception(e);
//                    }

                }
            }
        }
    }

//    private void showImageUri(Uri uriImage) {
//        try {
////                        mImage.setImageURI(mUri);
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), extrasUri);
//            showImage(bitmap);
//        } catch (IOException e) {
//            EventFactory.INSTANCE.exception(e);
//        }
//    }

//    private void showImage(Bitmap image) {
//        mPhotoAdapter.addImage(image);
//        setupEnablePhotoButton();
//    }

    private void showImage(Uri uri) {
        mPhotoAdapter.addImage(uri);
        setupEnablePhotoButton();
    }

    private void setupEnablePhotoButton() {
        mPhotoButton.setEnabled(mPhotoAdapter.getItemCount() <= MAX_COUNT_PHOTO);
    }

    @Override
    public void setAddressFromCoordinates(@NotNull String address) {
        String currentAddress = mAddress.getText().toString();
        if (currentAddress.isEmpty() || (mAddressLoaded != null && mAddressLoaded.equals(currentAddress))) {
            mAddress.setText(address);
            mAddressLoaded = address;
        }
    }

    @Override
    public void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(int textRes) {
        Toast.makeText(this, getString(textRes), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void close() {
        Intent intent = new Intent();
        intent.putExtra(SITE, getCreatedSite());
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.alpha_in, R.anim.slide_right_out);
    }
}