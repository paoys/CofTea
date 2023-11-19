package com.example.coftea.Customer.advance_order;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.coftea.LocationListener.MyLocationListener;
import com.example.coftea.LocationListener.MyLocationListenerCallback;
import com.example.coftea.Order.OrderDialogFragment;
import com.example.coftea.Order.OrderDialogViewModel;
import com.example.coftea.OrderItemList.OrderItemListDialogFragment;
import com.example.coftea.OrderItemList.OrderItemListViewModel;
import com.example.coftea.OrderItemList.OrderItemListViewModelFactory;
import com.example.coftea.data.Product;
import com.example.coftea.databinding.FragmentAdvanceOrderBinding;
import com.example.coftea.utilities.UserProvider;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.location.LocationManager;
import android.provider.Settings;
public class AdvanceOrderFragment extends Fragment {
    private final UserProvider userProvider = UserProvider.getInstance();
    private ImageButton ibCartButton;
    private TextView tvOrderItemCount;
    private FragmentAdvanceOrderBinding binding;
    private CustomerAdvancedOrderAdapter customerAdvancedOrderAdapter;
    private AdvanceOrderViewModel advanceOrderViewModel;
    private OrderDialogViewModel orderDialogViewModel;
    private OrderItemListViewModel orderItemListViewModel;
    private ArrayList<Product> _products = new ArrayList<>();
    private OrderDialogFragment orderToCartDialogFragment;
    private OrderItemListDialogFragment orderItemListDialogFragment;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private void getAndDisplayCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        Location from = new Location("FROM");
                        from.setLatitude(latitude);
                        from.setLongitude(longitude);

                        Location to = new Location("TO");
                        to.setLatitude(14.861162049832275);
                        to.setLongitude(120.80947378369852);

                        float distance = from.distanceTo(to);

                        Log.e("LocationFragment111", String.valueOf(distance) + "m");
                        // Handle the latitude and longitude as needed
                        // For example, you can display them in the UI or use them in further processing

                        // Example: Displaying latitude and longitude in Logcat
                        // Make sure you have 'import android.util.Log;' at the top of your file

                        Log.e("LocationFragment", "Latitude: " + latitude + ", Longitude: " + longitude);
                    }
                });
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void showSettingsAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");

        alertDialog.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        });

        alertDialog.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        alertDialog.show();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Get and display the current location
            if(!isOnline()) {
                return;
            }
            if(!isLocationEnabled()) {
                showSettingsAlert();
                return;
            }
            getAndDisplayCurrentLocation();
        } else {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAdvanceOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        init();

        return root;
    }

    private void init(){

        String userName = userProvider.getUser().first;
        String userMobileNo = userProvider.getUser().second;

        ibCartButton = binding.ibCartButton;
        tvOrderItemCount = binding.tvOrderItemCount;

        RecyclerView rvCustomerProductList = binding.rvAdvanceOrderList;
        rvCustomerProductList.setHasFixedSize(true);
        rvCustomerProductList.setLayoutManager(new LinearLayoutManager(getContext()));

        ibCartButton.setEnabled(false);
        ibCartButton.setVisibility(View.GONE);
        tvOrderItemCount.setVisibility(View.GONE);

        advanceOrderViewModel = new ViewModelProvider(this).get(AdvanceOrderViewModel.class);
        orderDialogViewModel = new ViewModelProvider(this).get(OrderDialogViewModel.class);
        orderItemListViewModel = new ViewModelProvider(this, new OrderItemListViewModelFactory(userMobileNo)).get(OrderItemListViewModel.class);

        customerAdvancedOrderAdapter = new CustomerAdvancedOrderAdapter(_products, advanceOrderViewModel, orderDialogViewModel);
        orderToCartDialogFragment = new OrderDialogFragment(orderDialogViewModel);
        rvCustomerProductList.setAdapter(customerAdvancedOrderAdapter);

        orderItemListViewModel = new OrderItemListViewModel(userMobileNo);
        orderItemListDialogFragment = new OrderItemListDialogFragment(orderItemListViewModel, orderDialogViewModel);

        ibCartButton.setOnClickListener(view -> {
            OrderItemListDialogFragment existingFragment = (OrderItemListDialogFragment) getParentFragmentManager().findFragmentByTag("OrderItemListFragment");

            if (existingFragment == null)
                orderItemListDialogFragment.show(getParentFragmentManager(), "OrderItemListFragment");
            else
                existingFragment.getDialog().show();
        });
    }

    private void startListen(){
        ibCartButton.setVisibility(View.VISIBLE);
        tvOrderItemCount.setVisibility(View.VISIBLE);

        advanceOrderViewModel.productList.observe(getViewLifecycleOwner(), products -> {
            _products = products;
            customerAdvancedOrderAdapter.UpdateList(_products);
        });

        orderDialogViewModel.orderItem.observe(getViewLifecycleOwner(), orderItem -> {
            if (orderItem == null) return;
            OrderDialogFragment existingFragment = (OrderDialogFragment) getParentFragmentManager().findFragmentByTag("ItemToOrderFragment");

            if (existingFragment == null)
                orderToCartDialogFragment.show(getParentFragmentManager(), "ItemToOrderFragment");
            else
                existingFragment.getDialog().show();
        });

//        OrderItemDialogPlus orderItemDialogPlus = new OrderItemDialogPlus(getViewLifecycleOwner(), getContext(), orderItemDialogViewModel);

        orderItemListViewModel.orderItems.observe(getViewLifecycleOwner(), orderItems -> {
            ibCartButton.setEnabled(orderItems.size() != 0);
            tvOrderItemCount.setText(String.valueOf(orderItems.size()));
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}
