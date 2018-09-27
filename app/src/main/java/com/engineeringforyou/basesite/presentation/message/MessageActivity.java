package com.engineeringforyou.basesite.presentation.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.presentation.message.presenter.MessagePresenter;
import com.engineeringforyou.basesite.presentation.message.presenter.MessagePresenterImpl;
import com.engineeringforyou.basesite.presentation.message.views.MessageView;
import com.engineeringforyou.basesite.utils.KeyBoardUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageActivity extends AppCompatActivity implements MessageView {


    @BindView(R.id.email)
    EditText mEmail;
    @BindView(R.id.message)
    EditText mMessage;
    @BindView(R.id.progress_bar)
    View mProgress;

    private MessagePresenter mPresenter;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, MessageActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        mPresenter = new MessagePresenterImpl(this);
        mPresenter.bind(this);
        initToolbar();
    }

    private void initToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @OnClick(R.id.send_button)
    public void sendMessage() {
        if (mMessage.getText().toString().trim().isEmpty()) {
            showMessage(R.string.enter_message);
            return;
        }
        KeyBoardUtils.hideKeyboard(this, getCurrentFocus());
        mPresenter.sendMessage(mEmail.getText().toString(), mMessage.getText().toString());
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
        finish();
        overridePendingTransition(R.anim.alpha_in, R.anim.slide_right_out);
    }
}