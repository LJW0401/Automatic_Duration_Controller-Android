package com.example.AutomaticDurationControl;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
/*开发过程中用到的库*/
import android.view.MenuItem;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
/*---------------*/

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    private BottomNavigationView bottomNavigationView;
    private FragmentContainerView fragmentContainerView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // 获取BottomNavigationView和FragmentContainerView的引用
        bottomNavigationView = view.findViewById(R.id.bottomMainNavigationView);
        fragmentContainerView = view.findViewById(R.id.fragmentMainContainerView);

        // 设置底部导航栏点击监听
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // 处理菜单项选择事件
                int id = item.getItemId();
                navigateToFragment(id); // 调用自定义方法来处理导航
                return true;
            }
        });

        // 设置默认Fragment
        if (savedInstanceState == null) {
            navigateToFragment(R.id.mainMenu_FunctionSelection); // 这里设置默认的Fragment，例如homeFragment
        }

        return view;
    }

    // 处理菜单项选择事件
    private void navigateToFragment(int menuItemId) {
        Fragment fragment = null;

        if (menuItemId == R.id.mainMenu_FunctionSelection) {
            fragment = new FunctionSelectionFragment();
        }else if (menuItemId == R.id.mainMenu_Help) {
            fragment = new HelpFragment();
        }else if (menuItemId == R.id.mainMenu_AccountInformation) {
            fragment = new AccountInformationFragment();
        }

        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentMainContainerView, fragment);
            fragmentTransaction.commit();
        }
    }
}