package cn.edu.fzu.daoyun_app.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.edu.fzu.daoyun_app.LoginActivity;
import cn.edu.fzu.daoyun_app.MainActivity;
import cn.edu.fzu.daoyun_app.ModifyPasActivity;
import cn.edu.fzu.daoyun_app.R;
import cn.edu.fzu.daoyun_app.UserInfoActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MeFragment extends Fragment {


    protected Button mBtnLogin;
    protected LinearLayout userInfoLayout;
    protected LinearLayout privacyLayout;
    protected Button logoutBtn;
    private TextView username;
    public static ImageView userIconIV;
    public static ImageView userInfoImg;
    public static File iconFile = null;
    private String path;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me,container,false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == getActivity().RESULT_OK){
            if(iconFile != null){
                Bitmap bitmap = BitmapFactory.decodeFile(iconFile.getAbsolutePath());
                userIconIV.setImageBitmap(bitmap);
            }
        }

        Log.i("MeFragmentInfo", "onActivityResult");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mBtnLogin= (Button) getView().findViewById(R.id.btn_login);
//        mBtnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //登录
//                Intent  login=new Intent(getActivity(),LoginActivity.class);
//                startActivity(login);
//            }
//        });
        Log.i("MeFragmentInfo", "onActivityCreated");
        path = Environment.getExternalStorageDirectory() + File.separator + "daoyun";
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        userIconIV = getActivity().findViewById(R.id.user_icon);
        initUi();

        userIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(getContext(), ShowIconActivity.class));
            }
        });
        username= getActivity().findViewById(R.id.user_name);
        username.setText(MainActivity.userName);
        userInfoLayout = getActivity().findViewById(R.id.layout_me_header);
        userInfoImg=getActivity().findViewById(R.id.detail_img);
        userInfoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserInfoActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout linearLayout = getActivity().findViewById(R.id.user_motify_layout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ModifyPasActivity.class);
                startActivity(intent);
            }
        });

        privacyLayout = getActivity().findViewById(R.id.prvacy_layout);
        privacyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(getContext(), PrivacyPolicyActivity.class));
            }
        });

        logoutBtn = getActivity().findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.userName = null;
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

    }

    public void initUi(){
//        if(!MainActivity.icon.equals("")){
//            iconFile = new File(path, MainActivity.icon);
//            if(iconFile.exists()){
//                Bitmap bitmap = BitmapFactory.decodeFile(iconFile.getAbsolutePath());
//                userIconIV.setImageBitmap(bitmap);
//            }else{
//                iconFile = null;
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        OkHttpClient okHttpClient = new OkHttpClient();
//                        RequestBody requestBody = new FormBody.Builder()
//                                .add("icon", MainActivity.icon)
//                                .add("type", "usericon")
//                                .build();
//                        Request request = new Request.Builder()
//                                .url("http://47.98.236.0:8080/downloadicon")
//                                .post(requestBody)
//                                .build();
//                        okHttpClient.newCall(request).enqueue(new Callback() {
//                            @Override
//                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//
//                            }
//
//                            @Override
//                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                                iconFile = new File(path, MainActivity.icon);
//                                if(iconFile.exists()){
//                                    iconFile.delete();
//                                }
//                                iconFile.createNewFile();
//                                FileOutputStream os = new FileOutputStream(iconFile);
//                                byte[] BytesArray = response.body().bytes();
//                                os.write(BytesArray);
//                                os.flush();
//                                os.close();
//                                final Bitmap bitmap = BitmapFactory.decodeFile(iconFile.getAbsolutePath());
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        userIconIV.setImageBitmap(bitmap);
//                                    }
//                                });
//
//                            }
//                        });
//                    }
//                }).start();
//            }
//        }else{
//            userIconIV.setImageResource(R.drawable.course_img_1);
//        }
    }

}
