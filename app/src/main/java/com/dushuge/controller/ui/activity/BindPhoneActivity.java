package com.dushuge.controller.ui.activity;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.dushuge.controller.R;
import com.dushuge.controller.base.BaseActivity;
import com.dushuge.controller.ui.utils.LoginUtils;
import com.dushuge.controller.ui.utils.MyShape;
import com.dushuge.controller.ui.utils.MyToash;
import com.dushuge.controller.ui.utils.TimeCount;
import com.dushuge.controller.utils.RegularUtlis;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

public class BindPhoneActivity extends BaseActivity {

    @BindView(R.id.activity_bind_phone_text)
    EditText mActivityBindPhoneText;
    @BindView(R.id.activity_bind_phone_clear)
    ImageView mActivityBindPhoneClear;
    @BindView(R.id.activity_bind_phone_message)
    EditText mActivityBindPhoneMessage;
    @BindView(R.id.activity_bind_phone_get_message_btn)
    TextView mActivityBindPhoneGetMessageBtn;
    @BindView(R.id.activity_bind_phone_container)
    LinearLayout mActivityBindPhoneContainer;
    @BindView(R.id.activity_bind_phone_btn)
    Button mActivityBindPhoneBtn;
    @BindView(R.id.activity_main_vitualkey)
    RelativeLayout mActivityMainVitualkey;
    @BindView(R.id.public_sns_topbar_back_img)
    ImageView mActivityBindPhoneImg;
    private LoginUtils loginUtils;

    @Override
    public int initContentView() {
        USE_PUBLIC_BAR = true;
        USE_EventBus = true;
        public_sns_topbar_title_id = R.string.UserInfoActivity_bind_phone;
        return R.layout.activity_bind_phone;
    }

    @Override
    public void initView() {
        TimeCount.getInstance().setActivity(mActivityBindPhoneText);
        // 设置工具类
        loginUtils = new LoginUtils(activity);
        loginUtils.setTimeCount(mActivityBindPhoneGetMessageBtn);
        mActivityBindPhoneBtn.setBackground(MyShape.setMyshapeMineStroke(activity, 20, 16));
        mActivityBindPhoneGetMessageBtn.setEnabled(false);
        mActivityBindPhoneBtn.setEnabled(false);
        mActivityBindPhoneMessage.setEnabled(false);
        mActivityBindPhoneText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0){
                    mActivityBindPhoneClear.setVisibility(View.GONE);
                }else {
                    mActivityBindPhoneClear.setVisibility(View.VISIBLE);
                    if (RegularUtlis.isMobile(s.toString())) {
                        mActivityBindPhoneGetMessageBtn.setEnabled(true);
                        if (!TimeCount.getInstance().IsRunIng) {
                            mActivityBindPhoneGetMessageBtn.setTextColor(ContextCompat.getColor(activity, R.color.red));
                            mActivityBindPhoneMessage.setEnabled(true);
                        }
                        setBtn(!TextUtils.isEmpty(mActivityBindPhoneMessage.getText().toString()));
                    } else {
                        mActivityBindPhoneMessage.setEnabled(false);
                        setBtn(false);
                        if (mActivityBindPhoneGetMessageBtn.isEnabled()) {
                            mActivityBindPhoneGetMessageBtn.setEnabled(false);
                            mActivityBindPhoneGetMessageBtn.setTextColor(ContextCompat.getColor(activity, R.color.gray_b0));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mActivityBindPhoneMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setBtn(!TextUtils.isEmpty(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mActivityBindPhoneText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mActivityBindPhoneClear.setVisibility(View.GONE);
                } else {
                    if (!TextUtils.isEmpty(mActivityBindPhoneText.getText().toString())) {
                        mActivityBindPhoneClear.setVisibility(View.VISIBLE);
                    } else {
                        mActivityBindPhoneClear.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @OnClick({R.id.public_sns_topbar_right_tv, R.id.public_sns_topbar_right_other, R.id.public_sns_topbar_right, R.id.public_sns_topbar_back_img,
            R.id.public_sns_topbar_back, R.id.public_sns_topbar_title, R.id.public_sns_topbar_layout, R.id.activity_bind_phone_text, R.id.activity_bind_phone_clear,
            R.id.activity_bind_phone_message, R.id.activity_bind_phone_get_message_btn, R.id.activity_bind_phone_container, R.id.activity_bind_phone_btn,
            R.id.activity_main_vitualkey})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.public_sns_topbar_back_img:
                finish();
                break;
            case R.id.activity_bind_phone_clear:
                mActivityBindPhoneText.setText("");
                break;
            case R.id.activity_bind_phone_get_message_btn:
                loginUtils.getMessage(mActivityBindPhoneText.getText().toString(), mActivityBindPhoneGetMessageBtn, true, null);
                break;
            case R.id.activity_bind_phone_btn:
                mActivityBindPhoneBtn.setEnabled(false);
                loginUtils.phoneUserBind(mActivityBindPhoneText.getText().toString(), mActivityBindPhoneMessage.getText().toString(), new LoginUtils.SignSuccess() {
                    @Override
                    public void success(String str) {
                        mActivityBindPhoneBtn.setEnabled(true);
                        if (str != null) {
                            finish();
                        }
                    }
                });
                break;
        }
    }

    /**
     * 设置btn
     * @param isEnable
     */
    private void setBtn(boolean isEnable) {
        if (isEnable) {
            mActivityBindPhoneBtn.setEnabled(true);
            mActivityBindPhoneBtn.setBackground(MyShape.setMyshapeMineStroke(activity, 20, 17));
            mActivityBindPhoneBtn.setTextColor(Color.WHITE);
        } else {
            mActivityBindPhoneBtn.setEnabled(false);
            mActivityBindPhoneBtn.setBackground(MyShape.setMyshapeMineStroke(activity, 20, 16));
            mActivityBindPhoneBtn.setTextColor(Color.GRAY);
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public void initInfo(String json) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNullRefresh() {

    }
}
