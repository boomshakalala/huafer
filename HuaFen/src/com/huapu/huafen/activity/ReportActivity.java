package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.HPCommentData;
import com.huapu.huafen.beans.HPReplyData;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.beans.ReportLabel;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.AliUpdateEvent;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.ImageUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.StringUtils;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.FootViewReportList;
import com.huapu.huafen.views.HeaderViewReport;
import com.squareup.okhttp.Request;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 举报投诉
 *
 * @author liang_xs
 * @version 1.6.0修改
 *          默认选中第一项：虚假、违法信息
 *          重复点击同一项：为选中这一项 ，即不能取消选中
 *          选中了一个 点击另一个时 交互效果：前一个取消选中状态，后一个被选中
 *          交互效果：选中状态“文字变红，有小红点”
 *          选填项：文字描述 和 图片
 *          必填项：举报原因
 */
public class ReportActivity extends BaseActivity implements OnItemClickListener {

    //	private Button btnCheckedUntruth,btnCheckedSpread,btnCheckedFraud,btnCheckedMeaningless;
    private ListView listView;
    private HeaderViewReport headerViewReport;
    private FootViewReportList footViewReportList;
    private MyListAdapter adapter;
    private ArrayList<ImageItem> selectBitmap = new ArrayList<ImageItem>();
    private String picKey;
    private int taskCount;
    private String reportImageUrls = "";
    private String reportLabelIds = "";
    private ArrayList<String> lables = new ArrayList<String>();
    private boolean isStop = false;
    /**
     * 1，商品 2，店铺,3,客服，4，留言，5，回复
     */
    private String type = "1";
    private String typeId = "";
    private GoodsInfo goodsInfo = new GoodsInfo();
    private List<ReportLabel> list = new ArrayList<ReportLabel>();
    private HPCommentData hpCommentData;
    private HPReplyData hpReplyData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        intViews();
        if (getIntent().hasExtra(MyConstants.EXTRA_REPORT_TYPE)) {
            type = getIntent().getStringExtra(MyConstants.EXTRA_REPORT_TYPE);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_USER_ID)) {
            typeId = getIntent().getStringExtra(MyConstants.EXTRA_USER_ID);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_GOODS_DETAIL)) {
            goodsInfo = (GoodsInfo) getIntent().getSerializableExtra(MyConstants.EXTRA_GOODS_DETAIL);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_HPCOMMENT_DATA)) {
            hpCommentData = (HPCommentData) getIntent().getSerializableExtra(MyConstants.EXTRA_HPCOMMENT_DATA);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_HPREPLY_DATA)) {
            hpReplyData = (HPReplyData) getIntent().getSerializableExtra(MyConstants.EXTRA_HPREPLY_DATA);
        }

        if (type.equals("1")) {
            setTitleString("举报");
            headerViewReport.layoutReport.setVisibility(View.VISIBLE);
            headerViewReport.layoutReportGoods.setVisibility(View.VISIBLE);
            headerViewReport.layoutReportComment.setVisibility(View.GONE);
            headerViewReport.tvReport.setText("举报商品");
            headerViewReport.tvReportReason.setText("举报原因");
            if (goodsInfo != null) {
                typeId = String.valueOf(goodsInfo.getGoodsId());
                headerViewReport.dlvGoodsName.setData(goodsInfo.getGoodsBrand(), goodsInfo.getGoodsName());
                if (goodsInfo.getGoodsImgs() != null && goodsInfo.getGoodsImgs().size() > 0) {
                    ImageLoader.loadImage(headerViewReport.ivReportGoods, goodsInfo.getGoodsImgs().get(0));
                }
            }
            picKey = DateTimeUtils.getYearMonthDayFolder("/report/");
        } else if (type.equals("2")) {
            setTitleString("投诉");
            headerViewReport.layoutReport.setVisibility(View.GONE);
            headerViewReport.tvReportReason.setText("为什么投诉");
            picKey = DateTimeUtils.getYearMonthDayFolder("/complaints/");
        } else if (type.equals("3")) {
            setTitleString("申请人工客服");
            picKey = DateTimeUtils.getYearMonthDayFolder("/arbitration/");
        } else if (type.equals("4")) {
            setTitleString("举报");
            headerViewReport.layoutReport.setVisibility(View.VISIBLE);
            headerViewReport.layoutReportGoods.setVisibility(View.GONE);
            headerViewReport.layoutReportComment.setVisibility(View.VISIBLE);
            headerViewReport.tvReport.setText("举报用户留言");
            headerViewReport.tvReportReason.setText("举报原因");
            if (hpCommentData != null) {
                if (hpCommentData.getComment() != null) {
                    typeId = String.valueOf(hpCommentData.getComment().getCommentId());
                }

                setHeaderImg(hpCommentData.getUser().getAvatarUrl());

                headerViewReport.ctvName.setData(hpCommentData.getUser());
                headerViewReport.tvTime.setText(DateTimeUtils.getMonthDayHourMinute(hpCommentData.getComment().getCreatedAt()));
                headerViewReport.tvContent.setText(hpCommentData.getComment().getContent());
            }
            picKey = DateTimeUtils.getYearMonthDayFolder("/report/");
        } else if (type.equals("5")) {
            setTitleString("举报");
            headerViewReport.layoutReport.setVisibility(View.VISIBLE);
            headerViewReport.layoutReportGoods.setVisibility(View.GONE);
            headerViewReport.layoutReportComment.setVisibility(View.VISIBLE);
            headerViewReport.tvReport.setText("举报用户留言");
            headerViewReport.tvReportReason.setText("举报原因");
            if (hpReplyData != null) {
                if (hpReplyData.getReply() != null) {
                    typeId = String.valueOf(hpReplyData.getReply().getReplyId());
                }

                setHeaderImg(hpReplyData.getUser().getAvatarUrl());

                headerViewReport.ctvName.setData(hpReplyData.getUser());
                headerViewReport.tvTime.setText(DateTimeUtils.getMonthDayHourMinute(hpReplyData.getReply().getCreatedAt()));
                headerViewReport.tvContent.setText(hpReplyData.getReply().getContent());
            }
            picKey = DateTimeUtils.getYearMonthDayFolder("/report/");
        } else if (type.equals("6")) {
            setTitleString("举报");
            headerViewReport.layoutReport.setVisibility(View.VISIBLE);
            headerViewReport.layoutReportGoods.setVisibility(View.GONE);
            headerViewReport.layoutReportComment.setVisibility(View.VISIBLE);
            headerViewReport.tvReport.setText("举报花语");
            headerViewReport.tvReportReason.setText("举报原因");
            if (hpReplyData != null) {
                if (hpReplyData.getReply() != null) {
                    typeId = String.valueOf(hpReplyData.getReply().getReplyId());
                }

                setHeaderImg(hpReplyData.getUser().getAvatarUrl());

                headerViewReport.ctvName.setData(hpReplyData.getUser());
                headerViewReport.tvTime.setText(DateTimeUtils.getMonthDayHourMinute(hpReplyData.getReply().getCreatedAt()));
                headerViewReport.tvContent.setText(hpReplyData.getReply().getContent());
            }
            picKey = DateTimeUtils.getYearMonthDayFolder("/report/");
        } else if (type.equals("8")) {
            setTitleString("举报");
            headerViewReport.layoutReport.setVisibility(View.VISIBLE);
            headerViewReport.layoutReportGoods.setVisibility(View.GONE);
            headerViewReport.layoutReportComment.setVisibility(View.VISIBLE);
            headerViewReport.tvReport.setText("举报花语");
            headerViewReport.tvReportReason.setText("举报原因");
            if (hpReplyData != null) {
                if (hpReplyData.getReply() != null) {
                    typeId = String.valueOf(hpReplyData.getReply().getReplyId());
                }

                setHeaderImg(hpReplyData.getUser().getAvatarUrl());

                headerViewReport.ctvName.setData(hpReplyData.getUser());
                headerViewReport.tvTime.setText(DateTimeUtils.getMonthDayHourMinute(hpReplyData.getReply().getCreatedAt()));
                headerViewReport.tvContent.setText(hpReplyData.getReply().getContent());
            }
            picKey = DateTimeUtils.getYearMonthDayFolder("/report/");
        }
        startRequestForGetUserSellGoodsPagelistList();
    }

    private void setHeaderImg(String avatarUrl) {
        ImageLoader.resizeSmall(headerViewReport.ivHeader, avatarUrl, 1);
    }

    private void intViews() {
        getTitleBar().
                setTitle("举报").
                setRightText("提交", this);
        listView = (ListView) findViewById(R.id.listView);
        headerViewReport = new HeaderViewReport(this);
        footViewReportList = new FootViewReportList(this);
        footViewReportList.notifyAdapter(selectBitmap);
        listView.addHeaderView(headerViewReport);
        listView.addFooterView(footViewReportList);

        adapter = new MyListAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.tvTitleBarRight) {
            if (lables == null || lables.size() == 0) {
                toast("请选择理由");
                return;
            }

            getTitleBar().getTitleTextRight().setOnClickListener(null);
            ProgressDialog.showProgress(ReportActivity.this);
            isStop = false;
            if (selectBitmap != null && selectBitmap.size() > 0) {
                upload2Ali();
            } else {
                startRequestForSaveReportInfo();
            }
        }
    }

    /**
     * 获取投诉理由
     */
    private void startRequestForGetUserSellGoodsPagelistList() {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(ReportActivity.this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", type);
        LogUtil.i("liang", "parmas:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.GETREPORTREASON, params,
                new StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        ProgressDialog.closeProgress();
                    }

                    @Override
                    public void onResponse(String response) {
                        ProgressDialog.closeProgress();
                        LogUtil.i("liang", "商品投诉理由列表:" + response);
                        JsonValidator validator = new JsonValidator();
                        boolean isJson = validator.validate(response);
                        if (!isJson) {
                            return;
                        }
                        try {
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                if (!TextUtils.isEmpty(baseResult.obj)) {
                                    list = ParserUtils.parserReportReasonListData(baseResult.obj);
                                    if (list != null) {
                                        list.get(0).setSelect(true);
                                        lables.add(String.valueOf(list.get(0).getLabelId()));
                                        adapter.setData(list);
                                    }
                                }
                            } else {
                                CommonUtils.error(baseResult, ReportActivity.this, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });
    }


    class MyListAdapter extends BaseAdapter {

        private List<ReportLabel> list = new ArrayList<ReportLabel>();
        private Context mContext;

        public MyListAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        public void setData(List<ReportLabel> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_listview_report, null);
                viewHolder = new ViewHolder();
                viewHolder.tvReportTitle = (TextView) convertView.findViewById(R.id.tvReportTitle);
                viewHolder.btnChecked = (Button) convertView.findViewById(R.id.btnChecked);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvReportTitle.setText(String.valueOf(list.get(position).getLabel()));
            if (list.get(position).isSelect()) {
                viewHolder.btnChecked.setSelected(true);
                viewHolder.tvReportTitle.setTextColor(getResources().getColor(R.color.base_pink));
            } else {
                viewHolder.btnChecked.setSelected(false);
                viewHolder.tvReportTitle.setTextColor(getResources().getColor(R.color.text_color));
            }
            return convertView;
        }

        class ViewHolder {
            public TextView tvReportTitle;
            public Button btnChecked;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Object item = parent.getItemAtPosition(position);
        if (item instanceof ReportLabel) {
            ReportLabel bean = (ReportLabel) item;
            if (bean.isSelect()) {
//				bean.setSelect(false);
//				lables.remove(String.valueOf(bean.getLabelId()));
            } else {
                for (ReportLabel reportLabel : list) {
                    reportLabel.setSelect(false);
                }
                lables = new ArrayList<String>();
                bean.setSelect(true);
                lables.add(String.valueOf(bean.getLabelId()));
            }
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY) {
                selectBitmap = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                if (selectBitmap != null) {
//					footViewReportList.adapter.setData(selectBitmap);
                    footViewReportList.notifyAdapter(selectBitmap);
                }
                LogUtil.i("liangxs", "1111");
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_MAIN_TO_CAMERA) {
                // 判断图片是否旋转，处理图片旋转
                ExifInterface exif;
                Bitmap bitmap = ImageUtils.getLoacalBitmap(ReportActivity.this, footViewReportList.imgPath);
                try {
                    exif = new ExifInterface(footViewReportList.imgPath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                    // 旋转图片，使其显示方向正常
                    Bitmap newBitmap = ImageUtils.getTotateBitmap(bitmap, orientation);
                    File out = new File(footViewReportList.imgPath);
                    boolean save = ImageUtils.saveMyBitmap(out, newBitmap);
                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    if (newBitmap != null) {
                        newBitmap.recycle();
                        newBitmap = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ImageItem item = new ImageItem();
                item.imagePath = footViewReportList.imgPath;
                item.isSelected = true;
                selectBitmap.add(item);
//				footViewReportList.adapter.setData(selectBitmap);
                footViewReportList.notifyAdapter(selectBitmap);
                LogUtil.i("liangxs", "2222");
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_COMMENT_TO_ALBUM) {
                selectBitmap = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                if (selectBitmap != null) {
//					footViewReportList.adapter.setData(selectBitmap);
                    footViewReportList.notifyAdapter(selectBitmap);
                    LogUtil.i("liangxs", "3333");
                }
            }
        }

    }


    private Handler taskHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    taskCount--;
                    String key = (String) msg.obj;
//				HashMap<String, String> a = new HashMap<String, String>();
//				a.put("url", key);
//				map.add(a);
                    reportImageUrls += MyConstants.OSS_IMG_HEAD + key + "@!logo" + ",";
                    if (taskCount == 0) {
                        startRequestForSaveReportInfo();
                    }
                    break;

                default:
                    break;
            }

        }

        ;
    };


    /**
     * 上传图片到阿里服务器
     */
    private void upload2Ali() {
        taskCount = selectBitmap.size();
        for (ImageItem item : selectBitmap) {
            if (isStop) {
                return;
            }
            UploadPicTask task = new UploadPicTask();
            task.execute(item.imagePath);
        }
    }


    /**
     * 投诉
     */
    private void startRequestForSaveReportInfo() {
        if (!CommonUtils.isNetAvaliable(this)) {
            taskCount = selectBitmap.size();
            isStop = true;
            reportImageUrls = "";
            ProgressDialog.closeProgress();
            getTitleBar().getTitleTextRight().setOnClickListener(ReportActivity.this);
            toast("请检查网络连接");
            return;
        }
        for (String id : lables) {
            reportLabelIds += id + ",";
        }
        HashMap<String, String> params = new HashMap<String, String>();
        if (!TextUtils.isEmpty(reportImageUrls)) {
            reportImageUrls = StringUtils.substringBeforeLast(reportImageUrls, ",");
            params.put("reportImageUrls", reportImageUrls);
        }
        reportLabelIds = StringUtils.substringBeforeLast(reportLabelIds, ",");
        params.put("userId", String.valueOf(CommonPreference.getUserId()));
        params.put("type", type);
        params.put("typeId", typeId);
        params.put("reportLabelIds", reportLabelIds);
        params.put("reportContent", footViewReportList.etTopWord.getText().toString());
        LogUtil.i("liang", "投诉params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.SAVEREPORTINFO, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.i("liang", "投诉：" + e.toString());
                taskCount = selectBitmap.size();
                isStop = true;
                reportImageUrls = "";
                ProgressDialog.closeProgress();
                getTitleBar().getTitleTextRight().setOnClickListener(ReportActivity.this);
                toast("Error，请重试");
            }


            @Override
            public void onResponse(String response) {
                taskCount = selectBitmap.size();
                isStop = true;
                reportImageUrls = "";
                getTitleBar().getTitleTextRight().setOnClickListener(ReportActivity.this);
                LogUtil.i("liang", "投诉：" + response.toString());
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        FileUtils.delAllFile(FileUtils.getIconDir());
                        // 投诉成功
                        ReportActivity.this.finish();
                    } else {
                        CommonUtils.error(baseResult, ReportActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private class UploadPicTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            String urlPath = (String) params[0];
            String fileName = System.currentTimeMillis() + ".jpg";
            String folder = MyConstants.OSS_FOLDER_BUCKET;
            String key = picKey + fileName;
            Bitmap bm = ImageUtils.revitionImageSize(urlPath);
            if (bm != null) {
                String picPath = FileUtils.saveBitmap(bm, FileUtils.getIconDir(), fileName);
                AliUpdateEvent updateEvent = new AliUpdateEvent(ReportActivity.this, picPath, folder, key);
                PutObjectResult result = updateEvent.putObjectFromLocalFile();
                if (result != null) {
                    response = key;
                }
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!TextUtils.isEmpty(result)) {
                Message msg = new Message();
                msg.what = 0;
                msg.obj = result;
                taskHandler.sendMessage(msg);
            } else {
                taskCount = selectBitmap.size();
                isStop = true;
                reportImageUrls = "";
                ProgressDialog.closeProgress();
                getTitleBar().getTitleTextRight().setOnClickListener(ReportActivity.this);
                toast("图片上传失败，请重试");
            }
        }
    }
}
