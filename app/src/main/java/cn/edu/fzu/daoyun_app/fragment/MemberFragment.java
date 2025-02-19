package cn.edu.fzu.daoyun_app.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.addapp.pickers.listeners.OnMoreItemPickListener;
import cn.addapp.pickers.picker.LinkagePicker;
import cn.addapp.pickers.util.DateUtils;
import cn.edu.fzu.daoyun_app.ClassTabActivity;
import cn.edu.fzu.daoyun_app.Config.GConfig;
import cn.edu.fzu.daoyun_app.Config.UrlConfig;
import cn.edu.fzu.daoyun_app.CreateClassActivity;
import cn.edu.fzu.daoyun_app.FinishSignInActivity;
import cn.edu.fzu.daoyun_app.LimitTimeSignInAcitvity;
import cn.edu.fzu.daoyun_app.MainActivity;
import cn.edu.fzu.daoyun_app.Member;
import cn.edu.fzu.daoyun_app.OneBtnSignInActivity;
import cn.edu.fzu.daoyun_app.OneBtnSignInSettingActivity;
import cn.edu.fzu.daoyun_app.R;
import cn.edu.fzu.daoyun_app.SignInRecoderActivity;
import cn.edu.fzu.daoyun_app.Utility;
import cn.edu.fzu.daoyun_app.Utils.AlertDialogUtil;
import cn.edu.fzu.daoyun_app.Utils.OkHttpUtil;
import cn.edu.fzu.daoyun_app.adapter.MemberAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MemberFragment extends Fragment {
    public List<Member> memberList = new ArrayList<>();
    private LinearLayout linearLayout;
    private LinearLayout affectionCardLayout;
    private LinearLayout groupPlanLayout;
    private TextView signInTV;
    private TextView memberSumTV;
    private Button backBtn;
    private ListView listView;
    public MemberAdapter memberAdapter;
    private List<Integer> experienceList = new ArrayList<>();
    private List<Integer> indexList = new ArrayList<>();
    private int userMark;
    private Dialog bottomDialog;
    private String userExperienceScore;
    private TextView rankTV;
    public TextView experTV;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Member> tempMemberList = new ArrayList<>();
    public LocationClient locationClient;
    public double longitude = 0.0;
    public double latitude = 0.0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member, container, false);
        backBtn = view.findViewById(R.id.toolbar_left_btn);
        memberSumTV = view.findViewById(R.id.member_sum_Tv);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initMember();

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        rankTV = getActivity().findViewById(R.id.ranking_Tv);
        experTV = getActivity().findViewById(R.id.experience_temp_Tv);
        if (ClassTabActivity.enterType.equals("create")) {
            rankTV.setVisibility(View.GONE);
            experTV.setVisibility(View.GONE);
        }

        signInTV = getActivity().findViewById(R.id.signin_Tv);
        if (ClassTabActivity.enterType.equals("create")) {
            signInTV.setText("发起签到");
        }

        linearLayout = getActivity().findViewById(R.id.signin_layout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SignInRecoderActivity.class);
                                                startActivity(intent);

            }
        });

        affectionCardLayout = getActivity().findViewById(R.id.affection_card_layout);
        affectionCardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog("暂不支持\"心意卡片\"功能");
            }
        });

        groupPlanLayout = getActivity().findViewById(R.id.group_plan_layout);
        groupPlanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog("暂不支持\"小组方案\"功能");
            }
        });

    }

    public void refresh() {
        initMember();
        memberAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }



    public void onTimePicker(View view) {
        LinkagePicker.DataProvider provider = new LinkagePicker.DataProvider() {

            @Override
            public boolean isOnlyTwo() {
                return true;
            }

            @Override
            public List<String> provideFirstData() {
                ArrayList<String> firstList = new ArrayList<>();
                for (int i = 0; i <= 23; i++) {
                    String str = DateUtils.fillZero(i);
                    firstList.add(str);
                }
                return firstList;
            }

            @Override
            public List<String> provideSecondData(int firstIndex) {
                ArrayList<String> secondList = new ArrayList<>();
                for (int i = 0; i <= 59; i++) {
                    String str = DateUtils.fillZero(i);

                    secondList.add(str);
                }
                return secondList;
            }

            @Override
            public List<String> provideThirdData(int firstIndex, int secondIndex) {
                return null;
            }

        };
        LinkagePicker picker = new LinkagePicker(getActivity(), provider);
        picker.setCanLoop(false);
        picker.setGravity(Gravity.BOTTOM);
        picker.setLabel("签到时长", "");
        picker.setLineVisible(true);
        picker.setHeight(700);
        picker.setSelectedIndex(0, 8);
        picker.setAnimationStyle(R.style.Animation_CustomPopup);
        //int i = Integer.parseInt( first );
        //picker.setSelectedItem("12", "9");
        picker.setOnMoreItemPickListener(new OnMoreItemPickListener<String>() {

            @Override
            public void onItemPicked(String first, String second, String third) {
                int i = Integer.parseInt(first);
                int j = Integer.parseInt(second);
                int s = i * 60 + j;
                limitSignin(s);
                //showAlertDialog(first + "-" + second);
            }
        });
        picker.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Looper.prepare();
        bottomDialog.dismiss();
        Looper.loop();
        initMember();
    }


    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            latitude = bdLocation.getLatitude();
            longitude = bdLocation.getLongitude();
        }
    }

    private void limitSignin(int minute) {
        locationClient = new LocationClient(getContext());
        locationClient.registerLocationListener(new MyLocationListener());
        locationClient.start();
        //double latitude = bdLocation.getLatitude();
        //double longitude = bdLocation.getLongitude();
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
                json.put("cNumber", ClassTabActivity.classId);
                json.put("peId", MainActivity.peid);
                json.put("type", "2");
                json.put("limitdis", "100");
                json.put("value", "2");
                json.put("position_y", longitude);
                json.put("position_x", latitude);
                json.put("limitime", minute);


                OkHttpUtil.getInstance().PostWithJson(UrlConfig.getUrl(UrlConfig.UrlType.SEND_SIGNIN), json, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.i("错误的返回", e.getMessage());
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        final String responseBodyStr = response.body().string();
                        Log.i("sendsingininfo", responseBodyStr);
                        //String signinId;
                        com.alibaba.fastjson.JSONObject messjsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseBodyStr);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity())
//                                        .setMessage("一键签到设置成功！")
//                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseBodyStr);
                            Log.i("limitsignininfo", jsonObject.toString());
                            Log.i("secondinfo", String.valueOf(minute * 60));
                            String signinId = jsonObject.getJSONObject("data").getJSONObject("sendSignIn").getString("ssId").toString();
                            startActivity(new Intent(getActivity(), FinishSignInActivity.class)
                                    .putExtra("signin_mode", "2")
                                    .putExtra("second", String.valueOf(minute * 60))
                                    .putExtra("signinId", signinId));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//
//                                            }
//                                        });
//                                builder.show();
//                            }
//                        });
                    }
                });
            }
        }).start();

    }

    private void showPopupMenu(View view) {
        Looper.prepare();
        new XPopup.Builder(getContext())
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .asBottomList("", new String[]{"一键签到", "限时签到"},
                        //  null, 2,//带选中效果
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "一键签到":
                                        //bottomDialog.dismiss();
                                        //进入一键签到设置
                                        startActivity(new Intent(getContext(), OneBtnSignInSettingActivity.class));
                                        break;
//
                                    case "限时签到":
                                        //  bottomDialog.dismiss();
                                        //limitSignin(11)
                                         onTimePicker(view);
                                        //进入手势签到设置
                                        //  startActivity(new Intent(getContext(), GestureSettingActivity.class));
                                        break;
                                }
                                // toast("click " + text);
                            }
                        })
                .show();
          Looper.loop();

    }

    public void setDialog() {
//        Looper.prepare();
        bottomDialog = new Dialog(getContext(), R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.signin_bottom_dialog, null);
        root.findViewById(R.id.btn_gesture_signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
                //进入手势签到设置
                //  startActivity(new Intent(getContext(), GestureSettingActivity.class));
            }
        });
        root.findViewById(R.id.btn_one_btn_signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
                //进入一键签到设置
                startActivity(new Intent(getContext(), OneBtnSignInSettingActivity.class));
            }
        });
        bottomDialog.setContentView(root);
        Window dialogWindow = bottomDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();

        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        bottomDialog.show();
        //  Looper.loop();
    }

    public void showAlertDialog(final String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setMessage(msg)
                        .setPositiveButton("确定", null);
                builder.show();
            }
        });
    }

    public void initMember() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                memberList.clear();
                indexList.clear();
                tempMemberList.clear();
                experienceList.clear();
                com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
                json.put("cNumber", ClassTabActivity.classId);
                OkHttpUtil.getInstance().PostWithJson(UrlConfig.getUrl(UrlConfig.UrlType.CLASS_STUDENT), json, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.i("错误的返回", e.getMessage());
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseBodyStr = response.body().string();
                        Log.i("TaskReInfo", responseBodyStr);
                        if (responseBodyStr.contains("该班课下没有用户加入")) {

                        } else {
                            try {
                                parseJoinedList(responseBodyStr);
                                afterAction();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
    }

    public void parseJoinedList(String JsonArrayData) throws JSONException {
        JSONObject obj = new JSONObject(JsonArrayData);
        JSONObject objLocation = obj.getJSONObject("data");
        JSONArray jsonArray = objLocation.getJSONArray("personCourseList");
        int rank = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            rank++;
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            final String phoneNumber = jsonObject.getString("peId");
            final String name = jsonObject.getString("peName");
            final String IDNumber = jsonObject.getString("peNumber");
            String experienceScore = "";
            Log.v("exinfo", jsonObject.getString("value"));
            if (jsonObject.getString("value") == "null") {
                experienceScore = "0";
                Log.v("exinfo", experienceScore);
            } else {
                experienceScore = jsonObject.getString("value");
            }
            //final String experienceScore = "0";
            if (phoneNumber.equals(MainActivity.peid)) {
                userMark = rank - 1;
                userExperienceScore = experienceScore;
            }
            experienceList.add(Integer.valueOf(experienceScore));
            indexList.add(rank - 1);
            //    if(icon.equals("")){
            Member member;
            if (name.equals("")) {
                member = new Member(String.valueOf(rank), R.drawable.course_img_1, phoneNumber, IDNumber, experienceScore);
            } else {
                member = new Member(String.valueOf(rank), R.drawable.course_img_1, name, IDNumber, experienceScore);
            }
            memberList.add(member);
            tempMemberList.add(member);
            //  memberAdapter.notifyDataSetChanged();
//            }else{
//                final File userIconFile = new File(Environment.getExternalStorageDirectory() + "/daoyun/" +icon);
//                if(!userIconFile.exists()){
//                    final int finalRank = rank;
//                    final int finalRank1 = rank;
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            OkHttpClient okHttpClient = new OkHttpClient();
//                            RequestBody requestBody = new FormBody.Builder()
//                                    .add("icon", icon)
//                                    .add("type", "usericon")
//                                    .build();
//                            Request request = new Request.Builder()
//                                    .url("http://47.98.236.0:8080/downloadicon")
//                                    .post(requestBody)
//                                    .build();
//                            okHttpClient.newCall(request).enqueue(new Callback() {
//                                @Override
//                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
//
//                                }
//
//                                @Override
//                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                                    FileOutputStream os = new FileOutputStream(userIconFile);
//                                    byte[] BytesArray = response.body().bytes();
//                                    os.write(BytesArray);
//                                    os.flush();
//                                    os.close();
////                                    afterAction(finalRank, userIconFile.getAbsolutePath(), name, IDNumber, experienceScore, phoneNumber);
//                                    Member member;
//                                    if(name.equals("")){
//                                        member = new Member(String.valueOf(finalRank1), userIconFile.getAbsolutePath(), phoneNumber, IDNumber, experienceScore+"经验值");
//                                    }else{
//                                        member = new Member(String.valueOf(finalRank1), userIconFile.getAbsolutePath(), name, IDNumber, experienceScore+"经验值");
//                                    }
//                                    memberList.add(member);
//                                    tempMemberList.add(member);
////                                    memberAdapter.notifyDataSetChanged();
//                                }
//                            });
//                        }
//                    }).start();
//                }else{
//                    Member member;
//                    if(name.equals("")){
//                        member = new Member(String.valueOf(rank), userIconFile.getAbsolutePath(), phoneNumber, IDNumber, experienceScore+"经验值");
//                    }else{
//                        member = new Member(String.valueOf(rank), userIconFile.getAbsolutePath(), name, IDNumber, experienceScore+"经验值");
//                    }
//                    memberList.add(member);
//                    tempMemberList.add(member);
////                    memberAdapter.notifyDataSetChanged();
//                }
//
//            }
        }
        final int finalRank2 = rank;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                memberSumTV.setText(finalRank2 + "人");
            }
        });
    }

    public void afterAction() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Collections.sort(memberList);
                Collections.sort(experienceList);
                Collections.reverse(experienceList);
                HashMap<String, String> rankDict = new HashMap<>();
                int nowRank = 1;
                for (int s : experienceList) {
                    if (rankDict.containsKey(String.valueOf(s)))
                        continue;
                    else {
                        rankDict.put(String.valueOf(s), String.valueOf(nowRank++));
                    }
                }
                for (int i = 0; i < memberList.size(); i++) {
                    String rank = rankDict.get(memberList.get(i).getExperience_score());
                    memberList.get(i).setRanking(rank);
                  //  AlertDialogUtil.showToastText(memberList.get(i).getMemberName(),getActivity());
                }

                rankTV.setText("第" + rankDict.get(userExperienceScore) + "名");
                experTV.setText("当前获得" + userExperienceScore + "经验值");

                memberAdapter = new MemberAdapter(getContext(), R.layout.member_item, memberList);
//                View view = getLayoutInflater().inflate(R.layout.fragment_member, null);
                listView = getActivity().findViewById(R.id.member_list_view);
                listView.setAdapter(memberAdapter);
                Utility.setListViewHeightBasedOnChildren(listView);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Member member = memberList.get(position);
                    }
                });
            }
        });
    }

}