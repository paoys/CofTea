package com.example.coftea.Customer.advance_order;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.coftea.Order.OrderDialogFragment;
import com.example.coftea.Order.OrderDialogViewModel;
import com.example.coftea.OrderItemList.CartItemListDialogFragment;
import com.example.coftea.OrderItemList.CartItemListViewModel;
import com.example.coftea.OrderItemList.CartItemListViewModelFactory;
import com.example.coftea.data.Product;
import com.example.coftea.databinding.FragmentAdvanceOrderBinding;
import com.example.coftea.utilities.UserProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.location.LocationManager;
import android.provider.Settings;

public class AdvanceOrderFragment extends Fragment {

    private static final double SHOP_LATITUDE = 14.823203862432846;
    private static final double SHOP_LONGITUDE = 120.90092092528322;
    private static final double MINIMUM_DISTANCE_TO_SHOP = 1;


    private final UserProvider userProvider = UserProvider.getInstance();
    private ImageButton ibCartButton;
    private TextView tvOrderItemCount;
    private FragmentAdvanceOrderBinding binding;
    private CustomerAdvancedOrderAdapter customerAdvancedOrderAdapter;
    private AdvanceOrderViewModel advanceOrderViewModel;
    private OrderDialogViewModel orderDialogViewModel;
    private CartItemListViewModel cartItemListViewModel;
    private ArrayList<Product> _products = new ArrayList<>();
    private OrderDialogFragment orderToCartDialogFragment;
    private CartItemListDialogFragment cartItemListDialogFragment;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private LinearLayout llAdvanceOrderOutOfRange;
    private TextView tvAdvanceOrderDistanceToCofTea;
    private Button btnAdvanceOrderCheckDistance;
    private boolean isCheckingLocation = false;
    private void getAndDisplayCurrentLocation() {
        boolean hasNoAccessFineLocation = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        boolean hasNoAccessCourseLocation = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        if (hasNoAccessFineLocation && hasNoAccessCourseLocation) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    btnAdvanceOrderCheckDistance.setEnabled(true);
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        Location from = new Location("FROM");
                        from.setLatitude(latitude);
                        from.setLongitude(longitude);

                        Location to = new Location("TO");
                        to.setLatitude(SHOP_LATITUDE);
                        to.setLongitude(SHOP_LONGITUDE);

                        float distance = from.distanceTo(to);

                        if(distance <= MINIMUM_DISTANCE_TO_SHOP){
                            //startListen();
                            //llAdvanceOrderOutOfRange.setVisibility(View.GONE);
                            tvAdvanceOrderDistanceToCofTea.setText(String.format("%.2f",distance) +"m");
                            llAdvanceOrderOutOfRange.setVisibility(View.VISIBLE);
                        }
                        else{
                            //tvAdvanceOrderDistanceToCofTea.setText(String.format("%.2f",distance) +"m");
                            //llAdvanceOrderOutOfRange.setVisibility(View.VISIBLE);
                            startListen();
                            llAdvanceOrderOutOfRange.setVisibility(View.GONE);
                        }
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAdvanceOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        init();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(!isLocationEnabled())
                showSettingsAlert();

            if(isOnline())
                getAndDisplayCurrentLocation();
        } else {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }

        return root;
    }

    private void init(){

        String userName = userProvider.getUser().first;
        String userMobileNo = userProvider.getUser().second;

        ibCartButton = binding.ibCartButton;
        tvOrderItemCount = binding.tvOrderItemCount;

        llAdvanceOrderOutOfRange = binding.llAdvanceOrderOutOfRange;
        tvAdvanceOrderDistanceToCofTea = binding.tvAdvanceOrderDistanceToCofTea;
        btnAdvanceOrderCheckDistance = binding.btnAdvanceOrderCheckDistance;

        btnAdvanceOrderCheckDistance.setOnClickListener(view -> {
            view.setEnabled(false);
            getAndDisplayCurrentLocation();
        });

        RecyclerView rvCustomerProductList = binding.rvAdvanceOrderList;
        rvCustomerProductList.setHasFixedSize(true);
        rvCustomerProductList.setLayoutManager(new LinearLayoutManager(getContext()));

        ibCartButton.setEnabled(false);
        ibCartButton.setVisibility(View.GONE);
        tvOrderItemCount.setVisibility(View.GONE);

        advanceOrderViewModel = new ViewModelProvider(this).get(AdvanceOrderViewModel.class);
        orderDialogViewModel = new ViewModelProvider(this).get(OrderDialogViewModel.class);
        cartItemListViewModel = new ViewModelProvider(this, new CartItemListViewModelFactory(userMobileNo)).get(CartItemListViewModel.class);

        customerAdvancedOrderAdapter = new CustomerAdvancedOrderAdapter(_products, advanceOrderViewModel, orderDialogViewModel);
        orderToCartDialogFragment = new OrderDialogFragment(orderDialogViewModel);
        rvCustomerProductList.setAdapter(customerAdvancedOrderAdapter);

        cartItemListViewModel = new CartItemListViewModel(userMobileNo);
        cartItemListDialogFragment = new CartItemListDialogFragment(cartItemListViewModel, orderDialogViewModel);

        ibCartButton.setOnClickListener(view -> {
            CartItemListDialogFragment existingFragment = (CartItemListDialogFragment) getParentFragmentManager().findFragmentByTag("OrderItemListFragment");

            if (existingFragment == null)
                cartItemListDialogFragment.show(getParentFragmentManager(), "OrderItemListFragment");
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

        cartItemListViewModel.cartItems.observe(getViewLifecycleOwner(), orderItems -> {
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
