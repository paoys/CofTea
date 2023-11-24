package com.example.coftea.Cashier.queue;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.coftea.Order.OrderDialogFragment;
import com.example.coftea.R;
import com.example.coftea.data.OrderStatus;
import com.example.coftea.databinding.FragmentQueueBinding;
import com.example.coftea.utilities.SMSSender;

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
}
