package com.dushuge.controller.ui.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.dushuge.controller.R;
import com.dushuge.controller.base.BWNApplication;
import com.dushuge.controller.base.BaseActivity;
import com.dushuge.controller.constant.Api;
import com.dushuge.controller.ui.adapter.PushNoticationManagerAdapter;
import com.dushuge.controller.ui.push.PushManager;
import com.dushuge.controller.utils.LanguageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.dushuge.controller.ui.push.ReaderMessageReceiver.gotoNotificationSetting;
import static com.dushuge.controller.ui.push.ReaderMessageReceiver.isNotificationEnabled;

public class NotifycationManagerActivity extends BaseActivity {

    @BindView(R.id.activity_notify_auto)
    TextView activitySettingsAuto;
    @BindView(R.id.activity_settings_switch_container)
    LinearLayout activitySettingsSwitchContainer;
    @BindView(R.id.SettingsActivity_notify_set)
    TextView SettingsActivityNotifySet;

    @BindView(R.id.SettingsActivity_notify_list)
    ListView SettingsActivity_notify_list;

    private List<PushManager> pushManagerList;
    private PushNoticationManagerAdapter pushNoticationManagerAdapter;

    @Override
    public int initContentView() {
        USE_PUBLIC_BAR = true;
        public_sns_topbar_title_id = R.string.SettingsActivity_notify;
        return R.layout.activity_notifycationmanager;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isNotificationEnabled(activity)) {
            activitySettingsAuto.setText(LanguageUtil.getString(activity, R.string.SettingsActivity_notify_set_close));
            SettingsActivity_notify_list.setVisibility(View.GONE);
        } else {
            activitySettingsAuto.setText(LanguageUtil.getString(activity, R.string.SettingsActivity_notify_set_open));
            SettingsActivity_notify_list.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initView() {
        pushManagerList = new ArrayList<>();
        pushNoticationManagerAdapter = new PushNoticationManagerAdapter(activity, pushManagerList);
        SettingsActivity_notify_list.setAdapter(pushNoticationManagerAdapter);
    }

    @OnClick(R.id.activity_settings_switch_container)
    public void onViewClicked() {
        gotoNotificationSetting(BWNApplication.applicationContext.getActivity());
    }

    @Override
    public void initData() {
        httpUtils.sendRequestRequestParams(activity,Api.push_state, readerParams.generateParamsJson(),responseListener);
    }

    @Override
    public void initInfo(String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            String s = jsonObject.getString("list");
            Type type = new TypeToken<List<PushManager>>() {}.getType();
            List<PushManager> list = gson.fromJson(s, type);
            if (list != null && !list.isEmpty()) {
                pushManagerList.addAll(list);
                pushNoticationManagerAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
