package com.engineeringforyou.basesite.presentation.sitecreate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
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
import com.engineeringforyou.basesite.presentation.sitecreate.views.SiteCreateView;
import com.engineeringforyou.basesite.utils.Utils;
import com.google.android.gms.maps.model.CameraPosition;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.engineeringforyou.basesite.presentation.mapcoordinates.MapCoordinatesActivity.CODE_MAP;
import static com.engineeringforyou.basesite.presentation.mapcoordinates.MapCoordinatesActivity.LATITUDE;
import static com.engineeringforyou.basesite.presentation.mapcoordinates.MapCoordinatesActivity.LONGITUDE;

public class SiteCreateActivity extends AppCompatActivity implements SiteCreateView {

    private static final String POSITION = "position";
    public static final String SITE = "site";
    public static final int CODE_SITE_CREATE = 365;
    public static final int CODE_SITE_EDIT = 366;

    @BindView(R.id.site_operator_spinner)
    AppCompatSpinner mOperatorSpinner;
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
    @BindView(R.id.site_draft_button)
    Button mButton;

    private SiteCreatePresenter mPresenter;
    private Site mSite;

    public static void startForResult(Activity activity, @Nullable CameraPosition position) {
        Intent intent = new Intent(activity, SiteCreateActivity.class);
        intent.putExtra(POSITION, position);
        activity.startActivityForResult(intent, CODE_SITE_CREATE);
        activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out);
    }

    public static void startForEdit(Activity activity, Site site) {
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
    }

    private void setupViewForEdit() {
        mNumber.setText(mSite.getNumber());
        mLat.setText(String.valueOf(mSite.getLatitude()));
        mLong.setText(String.valueOf(mSite.getLongitude()));
        mAddress.setText(mSite.getAddress());
        mObject.setText(mSite.getObj());
        mOperatorSpinner.setSelection(Objects.requireNonNull(mSite.getOperator()).ordinal() + 1);
        mButton.setText(R.string.edit);
        mOperatorSpinner.setClickable(false);
        mSite.setLatitude(Double.parseDouble(mLat.getText().toString()));
        mSite.setLongitude(Double.parseDouble(mLong.getText().toString()));
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
    }

    @OnClick(R.id.site_draft_button)
    public void saveDraft() {
        if (mOperatorSpinner.getSelectedItemPosition() == 0) {
            showMessage(R.string.select_operator);
            return;
        }

        if (getLatitude() == null || getLongitude() == null) {
            showMessage(R.string.error_site_coordinates);
            return;
        }
        Site site = getCreatedSite();
        if (mSite != null) mPresenter.editSite(mSite, site, mUserName.getText().toString());
        else mPresenter.saveSite(site, mUserName.getText().toString());
    }

    private Site getCreatedSite() {
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
                Status.ACTIVE,
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_MAP) {
            if (resultCode == RESULT_OK) {
                Double lat = data.getDoubleExtra(LATITUDE, 0);
                Double lng = data.getDoubleExtra(LONGITUDE, 0);
                if (lat != 0 && lng != 0) {
                    mLat.setText(String.valueOf(lat));
                    mLong.setText(String.valueOf(lng));
                }
            }
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
        close();
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