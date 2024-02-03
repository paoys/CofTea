package com.example.coftea.Cashier.queue;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.coftea.Cashier.order.QueueEntry;
import com.example.coftea.Order.OrderDialogFragment;
import com.example.coftea.R;
import com.example.coftea.data.OrderStatus;
import com.example.coftea.databinding.FragmentQueueBinding;
import com.example.coftea.utilities.SMSSender;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QueueFragment extends Fragment {

    private FragmentQueueBinding binding;
    QueueViewModel queueViewModel;
    private RecyclerView rvCashierQueueOrderList;
    private QueueOrderToProcessDialogFragment queueOrderToProcessDialogFragment;
    private QueueOrderAdapter queueOrderAdapter;
    private Button btnQueueOrderPending, btnQueueOrderReady, btnQueueOrderDone, btnQueueOrderCancelled;

    private static final int SMS_PERMISSION_REQUEST_CODE = 123;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentQueueBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermission();
        }

        init();
        listen();

        //TextView queueNumberTextView = root.findViewById(R.id.tvQueueNumberValue);
        //if (queueNumberTextView != null) {
        //    int queueNumber = generateQueueNumber();
        //    String queueNumberText = "Queue #" + queueNumber;
        //    queueNumberTextView.setText(queueNumberText);
        //}



        return root;
    }

    private void init(){
        rvCashierQueueOrderList = binding.rvCashierQueueOrderList;

        btnQueueOrderCancelled = binding.btnQueueOrderCancelled;
        btnQueueOrderPending = binding.btnQueueOrderPending;
        btnQueueOrderReady = binding.btnQueueOrderReady;
        btnQueueOrderDone = binding.btnQueueOrderDone;

        btnQueueOrderPending.setEnabled(false);
        queueViewModel = new ViewModelProvider(this, new QueueViewModelFactory(OrderStatus.PENDING, getContext())).get(QueueViewModel.class);
        queueViewModel.changeOrderStatusFilter(OrderStatus.PENDING);

        rvCashierQueueOrderList = binding.rvCashierQueueOrderList;

        rvCashierQueueOrderList.setHasFixedSize(true);
        rvCashierQueueOrderList.setLayoutManager(new LinearLayoutManager(getContext()));

        queueOrderToProcessDialogFragment = new QueueOrderToProcessDialogFragment(queueViewModel);
        queueOrderAdapter = new QueueOrderAdapter(queueViewModel);
        rvCashierQueueOrderList.setAdapter(queueOrderAdapter);

        btnQueueOrderPending.setOnClickListener(view -> {
            queueViewModel.changeOrderStatusFilter(OrderStatus.PENDING);
            updateButtonState(btnQueueOrderPending);
        });
        btnQueueOrderReady.setOnClickListener(view -> {
            queueViewModel.changeOrderStatusFilter(OrderStatus.READY);
            updateButtonState(btnQueueOrderReady);
        });
        btnQueueOrderDone.setOnClickListener(view -> {
            queueViewModel.changeOrderStatusFilter(OrderStatus.DONE);
            updateButtonState(btnQueueOrderDone);
        });
        btnQueueOrderCancelled.setOnClickListener(view -> {
            queueViewModel.changeOrderStatusFilter(OrderStatus.CANCELLED);
            updateButtonState(btnQueueOrderCancelled);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_REQUEST_CODE);
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now send SMS
            } else {
                // Permission denied, handle accordingly
            }
        }
    }

    private void updateButtonState(Button clickedButton) {
        btnQueueOrderPending.setEnabled(true);
        btnQueueOrderReady.setEnabled(true);
        btnQueueOrderDone.setEnabled(true);
        btnQueueOrderCancelled.setEnabled(true);

        clickedButton.setEnabled(false);
    }
    private void listen(){
        queueViewModel.queueOrderList.observe(getViewLifecycleOwner(), queueOrder -> {
            if(queueOrder == null) return;
            queueOrderAdapter.UpdateList(queueOrder);
        });

        queueViewModel.queueOrderToProcess.observe(getViewLifecycleOwner(), queueEntry -> {
            if (queueEntry == null) return;
            OrderDialogFragment existingFragment = (OrderDialogFragment) getParentFragmentManager().findFragmentByTag("QueueOrderToDone");

            if (existingFragment == null)
                queueOrderToProcessDialogFragment.show(getParentFragmentManager(), "QueueOrderToDone");
            else
                existingFragment.getDialog().show();
        });

        queueViewModel.queueOrderToCancel.observe(getViewLifecycleOwner(), queueEntry -> {
            if(queueEntry == null) return;
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Cancel Order?");
            builder.setMessage("Are you sure you want to cancel " + queueEntry.getCustomerName()+"'s order?");
            builder.setPositiveButton("YES, CANCEL", (dialog, which) -> {
                queueViewModel.cancelQueueOrder();
                dialog.dismiss();
            });

            builder.setNegativeButton("Close", (dialog, which) -> {
                queueViewModel.cancelQueueOrder();
                dialog.dismiss();
            });

            builder.create().show();
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Method to generate a queue number based on the current date
    /*private int generateQueueNumber() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        String storedDate = getStoredDate();
        int storedQueueNumber = getStoredQueueNumber();

        if (storedDate != null && storedDate.equals(formattedDate)) {
            // Date matches, increment the stored queue number
            storedQueueNumber++;
            saveDateAndQueueNumber(formattedDate, storedQueueNumber);
            return storedQueueNumber;
        } else {
            // Date doesn't match, reset the queue number to 1
            saveDateAndQueueNumber(formattedDate, 1);
            return 1;
        }
    }

    // Method to save the date and queue number to SharedPreferences
    private void saveDateAndQueueNumber(String date, int queueNumber) {
        SharedPreferences preferences = requireContext().getSharedPreferences("QueuePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("storedDate", date);
        editor.putInt("queueNumber", queueNumber);
        editor.apply();
    }

    // Method to retrieve the stored date from SharedPreferences
    private String getStoredDate() {
        SharedPreferences preferences = requireContext().getSharedPreferences("QueuePrefs", Context.MODE_PRIVATE);
        return preferences.getString("storedDate", null);
    }

    // Method to retrieve the stored queue number from SharedPreferences
    private int getStoredQueueNumber() {
        SharedPreferences preferences = requireContext().getSharedPreferences("QueuePrefs", Context.MODE_PRIVATE);
        return preferences.getInt("queueNumber", 0);
    }*/
}
