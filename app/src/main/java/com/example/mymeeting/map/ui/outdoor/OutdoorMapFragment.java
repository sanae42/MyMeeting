package com.example.mymeeting.map.ui.outdoor;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.mymeeting.R;

import java.util.ArrayList;
import java.util.List;

import static cn.bmob.v3.Bmob.getApplicationContext;


public class OutdoorMapFragment extends Fragment {

    View root;

    private MapView mapView;

    public LocationClient mLocationClient;

    private TextView positionText;

    private BaiduMap baiduMap;

    private boolean isFirstLocate = true;

//    private RoutePlanSearch mSearch;


//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //????????????onCreate?????????????????????????????????
//        SDKInitializer.initialize(getApplicationContext());
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ///////////////////////////////////////////////////////////
        //????????????
        mapView = (MapView) root.findViewById(R.id.bmapView);
        //??????????????????
        baiduMap = mapView.getMap();
        //????????????????????????
        baiduMap.setMyLocationEnabled(true);

        //????????????
//        mSearch = RoutePlanSearch.newInstance();



        // ????????????????????????
        positionText = (TextView) root.findViewById(R.id.position_text_view);
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissions, 1);
        } else {
            requestLocation();
        }


        ///////////////////////////////////////////////////////////
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //???????????????????????????

        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getActivity().getApplicationContext());
        root = inflater.inflate(R.layout.fragment_outdoor, container, false);


        return root;
    }

//    OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener(){
//        @Override
//        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
//            //??????BikingRouteOverlay??????
//
//            WalkingRouteOverlay overlay = new WalkingRouteOverlay(baiduMap);
//            if (walkingRouteResult.getRouteLines().size() > 0) {
//                //????????????????????????,(????????????????????????????????????
//                //???BikingRouteOverlay??????????????????
//                overlay.setData(walkingRouteResult.getRouteLines().get(0));
//                //??????????????????BikingRouteOverlay
//                overlay.addToMap();
//            }
//        }
//
//        @Override
//        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
//
//        }
//
//        @Override
//        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
//
//        }
//
//        @Override
//        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
//
//        }
//
//        @Override
//        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
//
//        }
//
//        @Override
//        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
//
//        }
//    };

    //????????????????????????????????????MyLocationListener?????????
    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }


    //???????????????????????????????????????????????????????????????
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getContext(), "?????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                            return;
                        }
                    }
                    //????????????????????????
                    requestLocation();
                } else {
                    Toast.makeText(getContext(), "??????????????????", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;
            default:
        }
    }


    //?????????????????????
    private void navigateTo(BDLocation location) {
        //????????????????????????
        if (isFirstLocate) {
            //??????????????????????????????????????????
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);

            //??????Maker?????????
            LatLng point = new LatLng(39.956911, 116.347533);
            //??????Marker??????
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.location2);

            //??????MarkerOption???????????????????????????Marker
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap);
            //??????????????????Marker????????????
            baiduMap.addOverlay(option);


            isFirstLocate = false;
        }

        //??????????????????????????????
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // ?????????????????????????????????????????????????????????0-360
                .direction(location.getDirection()).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        baiduMap.setMyLocationData(locData);
    }


    //???????????????
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("?????????").append(location.getLatitude()).append("\n");
            currentPosition.append("?????????").append(location.getLongitude()).append("\n");
            currentPosition.append("?????????").append(location.getCountry()).append("\n");
            currentPosition.append("??????").append(location.getProvince()).append("\n");
            currentPosition.append("??????").append(location.getCity()).append("\n");
            currentPosition.append("??????").append(location.getDistrict()).append("\n");
            currentPosition.append("?????????").append(location.getStreet()).append("\n");
            currentPosition.append("???????????????");
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                currentPosition.append("GPS");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                currentPosition.append("??????");
            }
            //??????????????????????????????
            positionText.setText(currentPosition);

            //??????????????????????????????????????????????????????
            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                navigateTo(location);
            }
        }

    }

    //????????????????????????
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        //???5s??????????????????
        option.setScanSpan(5000);
        // ????????????????????????
        option.setIsNeedAddress(true);
        //??????????????????GCJ02?????????
        option.setCoorType("bd09ll");
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //fragment???????????????mLocationClient
        mLocationClient.stop();
        mapView.onDestroy();
        //fragment???????????????????????????????????????
        baiduMap.setMyLocationEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        //fragment Resume???Resume??????
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //fragment Pause???Pause??????
        mapView.onPause();
    }
}