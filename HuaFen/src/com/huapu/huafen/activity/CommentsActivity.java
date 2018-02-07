package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.HPagerAdapter;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.fragment.CommentListFragment;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.views.HViewPager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;


/**
 * 评论列表
 *
 * @author lalo.zhang
 */
public class CommentsActivity extends BaseActivity implements
        RadioGroup.OnCheckedChangeListener, CommentListFragment.OnCountChangedListener {

    @BindView(R2.id.rg)
    RadioGroup rg;
    @BindView(R2.id.rbAll)
    RadioButton rbAll;
    @BindView(R2.id.rbSatisfaction)
    RadioButton rbSatisfaction;
    @BindView(R2.id.rbGeneral)
    RadioButton rbGeneral;
    @BindView(R2.id.rbDissatisfied)
    RadioButton rbDissatisfied;
    @BindView(R2.id.pager)
    HViewPager pager;

    private long mId;
    private String mUrl;
    private String from;
    private long userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(MyConstants.COMMENT_LIST_ID)) {
            mId = intent.getLongExtra(MyConstants.COMMENT_LIST_ID, 0);
        }

        if (intent != null && intent.hasExtra(MyConstants.EXTRA_FROM_WHERE)) {
            from = intent.getStringExtra(MyConstants.EXTRA_FROM_WHERE);
        }

        if (intent != null && intent.hasExtra(MyConstants.COMMENT_LIST_URL)) {
            mUrl = intent.getStringExtra(MyConstants.COMMENT_LIST_URL);
        }

        if (intent != null && intent.hasExtra(MyConstants.EXTRA_USER_ID)) {
            userId = intent.getLongExtra(MyConstants.EXTRA_USER_ID, 0);
        }

        if (mId == 0 || TextUtils.isEmpty(from) || TextUtils.isEmpty(mUrl) || userId == 0) {
            final TextDialog dialog = new TextDialog(this, false);
            dialog.setContentText("参数缺失，请稍后重试");
            dialog.setLeftText("确定");
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    dialog.dismiss();
                    CommentsActivity.this.finish();
                }
            });
            dialog.show();
            return;
        }
        initView();
    }

    private void initView() {

        getTitleBar().setTitle("给TA的评价");
        rg.setOnCheckedChangeListener(this);
        pager.setCanFlip(false);
        pager.setOffscreenPageLimit(4);
        final ArrayList<Fragment> fragmentList = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putLong(MyConstants.COMMENT_LIST_ID, mId);
        bundle.putString(MyConstants.EXTRA_FROM_WHERE, from);
        bundle.putString(MyConstants.COMMENT_LIST_URL, mUrl);
        bundle.putLong(MyConstants.EXTRA_USER_ID, userId);


        for (int i = 0; i < 4; i++) {
            Bundle data = (Bundle) bundle.clone();
            if (i == 0) {
                data.putInt("satisfaction", 0);
            } else if (i == 1) {
                data.putInt("satisfaction", 30);
            } else if (i == 2) {
                data.putInt("satisfaction", 20);
            } else if (i == 3) {
                data.putInt("satisfaction", 10);
            }
            CommentListFragment listFragment = CommentListFragment.newInstance(data);
            listFragment.setOnCountChangedListener(this);
            fragmentList.add(listFragment);
        }

        HPagerAdapter pagerAdapter = new HPagerAdapter(getSupportFragmentManager(), fragmentList);
        pager.setAdapter(pagerAdapter);

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rbAll:
                pager.setCurrentItem(0);
                break;
            case R.id.rbSatisfaction:
                pager.setCurrentItem(1);
                break;
            case R.id.rbGeneral:
                pager.setCurrentItem(2);
                break;
            case R.id.rbDissatisfied:
                pager.setCurrentItem(3);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCountChange(TreeMap<String, Integer> map) {
        initRadioButtons(map);
    }

    private void initRadioButtons(TreeMap<String, Integer> map) {
        if (map != null && !map.isEmpty() && map.size() == 3) {
            Iterator<Map.Entry<String, Integer>> itt = map.entrySet().iterator();
            int all = 0;
            while (itt.hasNext()) {
                Map.Entry<String, Integer> entry = itt.next();
                int value = entry.getValue();
                String key = entry.getKey();
                if ("30".equals(key)) {
                    String ss1 = "好评";
                    String count = "\n%s";
                    String ss2 = String.format(count, String.valueOf(value));
                    SpannableString spannableString = new SpannableString(ss1 + ss2);
                    spannableString.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(12)), 0,
                            ss1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(12)), ss1.length(),
                            ss1.length() + ss2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    rbSatisfaction.setText(spannableString);

                } else if ("20".equals(key)) {
                    String ss1 = "中评";
                    String count = "\n%s";
                    String ss2 = String.format(count, String.valueOf(value));
                    SpannableString spannableString = new SpannableString(ss1 + ss2);
                    spannableString.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(12)), 0,
                            ss1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(12)), ss1.length(),
                            ss1.length() + ss2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    rbGeneral.setText(spannableString);
                } else if ("10".equals(key)) {
                    String ss1 = "差评";
                    String count = "\n%s";
                    String ss2 = String.format(count, String.valueOf(value));
                    SpannableString spannableString = new SpannableString(ss1 + ss2);
                    spannableString.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(12)), 0,
                            ss1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(12)), ss1.length(),
                            ss1.length() + ss2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    rbDissatisfied.setText(spannableString);
                }
                all += entry.getValue();
            }

            String ss1 = "全部";
            String count = "\n%s";
            String ss2 = String.format(count, String.valueOf(all));
            SpannableString spannableString = new SpannableString(ss1 + ss2);
            spannableString.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(12)), 0,
                    ss1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(12)), ss1.length(),
                    ss1.length() + ss2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            rbAll.setText(spannableString);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}