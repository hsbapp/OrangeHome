package com.gmoonxs.www.orangehome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.cg.hsb.HsbConstant;
import com.cg.hsb.HsbListener;
import com.cg.hsb.HsbScene;
import com.cg.hsb.HsbSceneListener;
import com.cg.hsb.Protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SceneFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SceneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SceneFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    HorizontalScrollView scene_fragment_scroll_view;
    GridView scene_fragment_grid_view;
    DisplayMetrics displayMetrics;
    private int NUM = 3; // 每行显示个数
    private int hSpacing = 20;// 水平间距

    private ImageView scene_fragment_add_scene_button;

    private ArrayList<HsbScene> hsbScenes;

    private Handler handler;

    private Protocol protocol;

    private final static int CONNECTED=1;
    private final static int SCENES_GET=2;
    private final static int HSBOFFLINE=3;

    public final static int ADD=1;
    public final static int EDIT=2;

    SceneGridViewAdapter adapter;

    LinearLayout scene_list_ready,scene_list_loading;

    private View view;

    //private LinearLayout tab_scene_back_home,tab_scene_leave_home,tab_scene_get_up_night,tab_scene_sleep,tab_scene_meeting,tab_scene_more;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SceneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SceneFragment newInstance(String param1, String param2) {
        SceneFragment fragment = new SceneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SceneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        init();

        OrangeHomeApplication.getOrangeHomeApplication().setHsbOffLineListener(new HsbOffLineListener() {
            @Override
            public void hsbOffLine() {
                Message message = new Message();
                message.what = HSBOFFLINE;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_scene, container, false);
        initView(view);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.scenceFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void scenceFragmentInteraction(Uri uri);
    }

    private void init(){
        hsbScenes=new ArrayList<>();
        handler=new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SCENES_GET:
                        getSceneList();
                        break;
                    case CONNECTED:
                        sceneReady();
                        break;
                    case HSBOFFLINE:
                        initView(view);
                        break;
                }
            }
        };


    }

    private void getScreenDen() {
        displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }


    private void setValue() {
        adapter = new SceneGridViewAdapter(getActivity().getApplicationContext(),getActivity(),hsbScenes);
        int count = adapter.getCount();
        int columns = (count % 2 == 0) ? count / 2 : count / 2 + 1;
        scene_fragment_grid_view.setAdapter(adapter);
        LayoutParams params = new LayoutParams(columns * displayMetrics.widthPixels / NUM, LayoutParams.WRAP_CONTENT);
        if (count <= 3) {
            scene_fragment_grid_view.setNumColumns(count);
            params = new LayoutParams(count * displayMetrics.widthPixels / NUM, LayoutParams.WRAP_CONTENT);
        }
        else {
            scene_fragment_grid_view.setNumColumns(columns);
        }
        scene_fragment_grid_view.setLayoutParams(params);
        scene_fragment_grid_view.setColumnWidth(displayMetrics.widthPixels / NUM);
        // gridView.setHorizontalSpacing(hSpacing);
        scene_fragment_grid_view.setStretchMode(GridView.NO_STRETCH);
    }

    private void setOnClickListener(){
        scene_fragment_add_scene_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText addNewSceneName = new EditText(getActivity());
                new AlertDialog.Builder(getActivity()).setTitle("请输入情景模式名称：")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(addNewSceneName)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String input = addNewSceneName.getText().toString();
                                if (input.equals("")) {
                                    Toast.makeText(getActivity().getApplicationContext(), getActivity().getBaseContext().getResources().getText(R.string.scence_fragment_add_scene_error), Toast.LENGTH_LONG).show();
                                } else {
                                    OrangeHomeApplication.getOrangeHomeApplication().setMiddleHsbScene(new HsbScene(input));
                                    Intent intent = new Intent(getActivity(), SceneActivity.class);
                                    //判断是新增模式还是编辑模式
                                    OrangeHomeApplication.getOrangeHomeApplication().setIsSceneEdit(true);
                                    intent.putExtra("type", ADD);
                                    startActivityForResult(intent, 0);
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();

            }
        });

        scene_fragment_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OrangeHomeApplication.getOrangeHomeApplication().setMiddleHsbScene(hsbScenes.get(i));
                if (hsbScenes.get(i).GetValid() == true) {
                    Intent intent = new Intent(getActivity(), SceneActivity.class);
                    //判断是新增模式还是编辑模式
                    intent.putExtra("type", EDIT);
                    OrangeHomeApplication.getOrangeHomeApplication().setIsSceneEdit(false);
                    startActivityForResult(intent, 0);
                } else {
                    Toast.makeText(getActivity(), getActivity().getBaseContext().getResources().getText(R.string.scene_fragment_scene_invalid), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void startGetSceneList(){
        protocol=OrangeHomeApplication.getOrangeHomeApplication().getmProto();
        protocol.SetSceneListener(new HsbSceneListener() {
            @Override
            public void onGetSceneResult(int errcode) {
                if (errcode == HsbConstant.HSB_E_OK) {
                    Message message = new Message();
                    message.what = SCENES_GET;
                    handler.sendMessage(message); //告诉主线程执行任务
                } else {
                }
            }

        });
        protocol.GetScene();
    }

    private void getSceneList(){
        hsbScenes=protocol.GetSceneList();
        adapter.notifyDataSetChanged();
        setValue();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == SceneActivity.RESULT_CODE) {
            startGetSceneList();
        }
    }


    private void sceneReady(){
        scene_list_loading.setVisibility(View.GONE);
        scene_list_ready.setVisibility(View.VISIBLE);
        startGetSceneList();
    }

    private void initView(View view){
        scene_fragment_scroll_view = (HorizontalScrollView) view.findViewById(R.id.scene_fragment_scroll_view);
        scene_fragment_grid_view = (GridView) view.findViewById(R.id.scene_fragment_grid_view);
        scene_fragment_add_scene_button=(ImageView)view.findViewById(R.id.scene_fragment_add_scene_button);
        scene_list_ready=(LinearLayout)view.findViewById(R.id.scene_list_ready);
        scene_list_ready.setVisibility(View.GONE);
        scene_list_loading=(LinearLayout)view.findViewById(R.id.scene_list_loading);
        scene_list_loading.setVisibility(View.VISIBLE);
        new Thread(new Runnable(){

            public void run(){
                try {
                    //当网关没有连接就绪 线程挂起
                    while ((!OrangeHomeApplication.getOrangeHomeApplication().isReady())||(!OrangeHomeApplication.getOrangeHomeApplication().getmProto().GetConnectivity())){
                        Thread.currentThread().sleep(1000);
                    }
                    Message message = new Message();
                    message.what = CONNECTED;
                    handler.sendMessage(message); //告诉主线程执行任务
                }catch (Exception e){

                }
            }

        }).start();

        scene_fragment_scroll_view.setHorizontalScrollBarEnabled(false);// 隐藏滚动条
        getScreenDen();
        setValue();
        setOnClickListener();
    }


    @Override
    public void onResume(){
        OrangeHomeApplication.getOrangeHomeApplication().setHsbOffLineListener(new HsbOffLineListener() {
            @Override
            public void hsbOffLine() {
                Message message = new Message();
                message.what = HSBOFFLINE;
                handler.sendMessage(message);
            }
        });
        if ((!OrangeHomeApplication.getOrangeHomeApplication().isReady())||(!OrangeHomeApplication.getOrangeHomeApplication().getmProto().GetConnectivity())){
            if(OrangeHomeApplication.getOrangeHomeApplication().getHsbOffLineListener()!=null){
                OrangeHomeApplication.getOrangeHomeApplication().getHsbOffLineListener().hsbOffLine();
            }
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        OrangeHomeApplication.getOrangeHomeApplication().setHsbOffLineListener(null);
        super.onPause();
    }

}
