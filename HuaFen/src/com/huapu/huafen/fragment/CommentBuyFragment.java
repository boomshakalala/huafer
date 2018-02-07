package com.huapu.huafen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.huapu.huafen.R;
import com.huapu.huafen.adapter.HPagerAdapter;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.views.HViewPager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by admin on 2016/11/3.
 */
public class CommentBuyFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener, CommentBuyListFragment.OnCountChangedListener {

    private RadioGroup rg;
    private HViewPager pager;
    private HPagerAdapter pagerAdapter;
    private RadioButton rbAll;
    private RadioButton rbSatisfaction;
    private RadioButton rbGeneral;
    private RadioButton rbDissatisfied;

    public static CommentBuyFragment newInstance(Bundle bundle) {
        CommentBuyFragment commentFragment = new CommentBuyFragment();
        commentFragment.setArguments(bundle);
        return commentFragment;
    }


    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onViewCreated(View root) {
        super.onViewCreated(root);
        initView(root);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void initView(View root) {
        rg = (RadioGroup) root.findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(this);
        rbAll = (RadioButton)root.findViewById(R.id.rbAll);
        rbSatisfaction = (RadioButton)root.findViewById(R.id.rbSatisfaction);
        rbGeneral = (RadioButton)root.findViewById(R.id.rbGeneral);
        rbDissatisfied = (RadioButton)root.findViewById(R.id.rbDissatisfied);

        pager = (HViewPager)root. findViewById(R.id.pager);
        pager.setCanFlip(false);
        pager.setOffscreenPageLimit(4);
        final ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();


        for(int i =0;i<4;i++){
            Bundle bundle = getArguments();
            Bundle data = (Bundle)bundle.clone();
            if(i ==0){
                data.putInt("satisfaction",0);
            }else if(i ==1){
                data.putInt("satisfaction",30);
            }else if(i ==2){
                data.putInt("satisfaction",20);
            }else if(i == 3){
                data.putInt("satisfaction",10);
            }
            CommentBuyListFragment listFragment = CommentBuyListFragment.newInstance(data);
            listFragment.setOnCountChangedListener(this);
            fragmentList.add(listFragment);
        }
        pagerAdapter = new HPagerAdapter(getChildFragmentManager(), fragmentList);
        pager.setAdapter(pagerAdapter);

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
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
        if(map!=null&&!map.isEmpty()&&map.size()==3){
            Iterator<Map.Entry<String, Integer>> itt = map.entrySet().iterator();
            int all = 0;
            while (itt.hasNext()){
                Map.Entry<String, Integer> entry = itt.next();
                int value = entry.getValue();
                String key = entry.getKey();
                if("30".equals(key)){
                    String ss1 = "满意";
                    String count = "\n(%s)";
                    String ss2 = String.format(count,String.valueOf(value));
                    SpannableString spannableString = new SpannableString(ss1+ss2);
                    spannableString.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(14)),0,ss1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(12)),ss1.length(),ss1.length()+ss2.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    rbSatisfaction.setText(spannableString);

                }else if("20".equals(key)){
                    String ss1 = "一般满意";
                    String count = "\n(%s)";
                    String ss2 = String.format(count,String.valueOf(value));
                    SpannableString spannableString = new SpannableString(ss1+ss2);
                    spannableString.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(14)),0,ss1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(12)),ss1.length(),ss1.length()+ss2.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    rbGeneral.setText(spannableString);
                }else if("10".equals(key)){
                    String ss1 = "不满意";
                    String count = "\n(%s)";
                    String ss2 = String.format(count,String.valueOf(value));
                    SpannableString spannableString = new SpannableString(ss1+ss2);
                    spannableString.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(14)),0,ss1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(12)),ss1.length(),ss1.length()+ss2.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    rbDissatisfied.setText(spannableString);
                }
                all += entry.getValue();
            }

            String ss1 = "全部";
            String count = "\n(%s)";
            String ss2 = String.format(count,String.valueOf(all));
            SpannableString spannableString = new SpannableString(ss1+ss2);
            spannableString.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(14)),0,ss1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(12)),ss1.length(),ss1.length()+ss2.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            rbAll.setText(spannableString);

        }
    }


}
