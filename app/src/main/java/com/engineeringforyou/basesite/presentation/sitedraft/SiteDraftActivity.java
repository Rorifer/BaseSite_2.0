package com.engineeringforyou.basesite.presentation.sitedraft;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.models.Operator;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.models.Status;
import com.engineeringforyou.basesite.presentation.mapcoordinates.MapCoordinatesActivity;
import com.engineeringforyou.basesite.presentation.sitedraft.presenter.SiteDraftPresenter;
import com.engineeringforyou.basesite.presentation.sitedraft.presenter.SiteDraftPresenterImpl;
import com.engineeringforyou.basesite.presentation.sitedraft.views.SiteDraftView;
import com.engineeringforyou.basesite.utils.EventFactory;
import com.google.android.gms.maps.model.CameraPosition;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.engineeringforyou.basesite.presentation.mapcoordinates.MapCoordinatesActivity.CODE_MAP;
import static com.engineeringforyou.basesite.presentation.mapcoordinates.MapCoordinatesActivity.LATITUDE;
import static com.engineeringforyou.basesite.presentation.mapcoordinates.MapCoordinatesActivity.LONGITUDE;

public class SiteDraftActivity extends AppCompatActivity implements SiteDraftView {

    private static final String POSITION = "position";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
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
    @BindView(R.id.progress_bar)
    View mProgress;

    private SiteDraftPresenter mPresenter;

    public static void start(Activity activity, @Nullable CameraPosition position) {
        Intent intent = new Intent(activity, SiteDraftActivity.class);
        intent.putExtra(POSITION, position);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_draft);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mPresenter = new SiteDraftPresenterImpl();
        mPresenter.bind(this);
        initToolbar();
        initSpinners();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initSpinners() {
        String[] operators = {Operator.MTS.getLabel(), Operator.MEGAFON.getLabel(),
                Operator.VIMPELCOM.getLabel(), Operator.TELE2.getLabel()};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, operators);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mOperatorSpinner.setAdapter(adapter);
        mOperatorSpinner.setPrompt("Операторы");
    }

    @OnClick(R.id.site_draft_button)
    public void saveDraft() {
        String number = mNumber.getText().toString().trim();
        Site site = new Site(
                null,
                Operator.values()[mOperatorSpinner.getSelectedItemPosition()],
                number,
                getLatitude(),
                getLongitude(),
                mAddress.getText().toString().trim(),
                mObject.getText().toString().trim(),
                number.concat("_").concat(String.valueOf(new Date().getTime())),
                Status.ACTIVE
        );
        mPresenter.saveDraft(site);
    }

    private Double getLongitude() {
        try {
            return Double.parseDouble(mLong.getText().toString().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            EventFactory.INSTANCE.exception(e);
            return null;
        }
    }

    private Double getLatitude() {
        try {
            return Double.parseDouble(mLat.getText().toString().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            EventFactory.INSTANCE.exception(e);
            return null;
        }
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
        finish();
        overridePendingTransition(R.anim.alpha_in, R.anim.slide_right_out);
    }
}