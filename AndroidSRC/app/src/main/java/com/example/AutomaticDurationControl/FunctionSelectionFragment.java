package com.example.AutomaticDurationControl;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FunctionSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FunctionSelectionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int OVERLAY_FLOATING_WINDOW_PERMISSION_REQUEST_CODE = 100;
    private static final int ACCESSIBLE_PERMISSION_REQUEST_CODE = 101;

    public FunctionSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FunctionSelectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FunctionSelectionFragment newInstance(String param1, String param2) {
        FunctionSelectionFragment fragment = new FunctionSelectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_function_selection, container, false);

        //设置获取无障碍服务权限的跳转
        Button Button_getAccessiblePermissions = view.findViewById(R.id.Button_getAccessiblePermissions);
        Button_getAccessiblePermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAccessiblePermissions();
            }
        });
        //设置获取悬浮窗权限的跳转
        Button Button_getFloatingWindowPermission = view.findViewById(R.id.Button_getFloatingWindowPermission);
        Button_getFloatingWindowPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFloatingWindowPermission();
            }
        });
        //TODO: 点击选择的功能后生成悬浮窗口并关闭主窗口
        //设置选择后生成悬浮窗
        Button Button_YouthLearning = view.findViewById(R.id.Button_YouthLearning);

        return view;
    }

    public void getAccessiblePermissions(){
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, ACCESSIBLE_PERMISSION_REQUEST_CODE);
    }

    public void getFloatingWindowPermission(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(requireContext())) {//check for alert window permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + requireActivity().getPackageName()));
                startActivityForResult(intent, OVERLAY_FLOATING_WINDOW_PERMISSION_REQUEST_CODE);
            }else{
                Toast.makeText(requireContext(), "已经有悬浮窗的权限啦！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_FLOATING_WINDOW_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(requireContext())) {
                // 用户没有授予悬浮窗权限
                Toast.makeText(requireContext(), "请给我悬浮窗的权限嘛QAQ", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(requireContext(), "谢谢你给我悬浮窗的权限", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == ACCESSIBLE_PERMISSION_REQUEST_CODE) {
            if (isAccessibilityServiceEnabled()){
                Toast.makeText(requireContext(), "谢谢你给我无障碍的权限", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(requireContext(), "请给我无障碍的权限嘛QAQ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isAccessibilityServiceEnabled() {
        String serviceName = getContext().getPackageName() + "/" + AutoClickService.class.getCanonicalName();
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    getContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            // 如果发生异常，说明无障碍服务未开启
            return false;
        }

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    getContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

            if (settingValue != null) {
                colonSplitter.setString(settingValue);
                while (colonSplitter.hasNext()) {
                    String accessibilityService = colonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(serviceName)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}