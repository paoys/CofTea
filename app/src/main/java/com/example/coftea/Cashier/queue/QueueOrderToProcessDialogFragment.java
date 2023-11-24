package com.example.coftea.Cashier.queue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.Cashier.order.CartItem;
import com.example.coftea.data.OrderStatus;
import com.example.coftea.databinding.DialogFragmentQueueOrderToProcessBinding;
import com.example.coftea.utilities.PHPCurrencyFormatter;

import java.util.ArrayList;


public class QueueOrderToProcessDialogFragment extends DialogFragment {
    private PHPCurrencyFormatter formatter = PHPCurrencyFormatter.getInstance();
    private DialogFragmentQueueOrderToProcessBinding binding;
    private QueueOrderToProcessAdapter queueOrderToProcessAdapter;
    private RecyclerView rvQueueOrderList;
    private Button btnQueueOrderReady, btnQueueOrderDone, btnQueueOrderClose;
    private TextView tvQueueOrderCustomerName, tvQueueOrderAmount;
    private QueueViewModel queueViewModel;

    public QueueOrderToProcessDialogFragment(QueueViewModel queueViewModel){
        this.queueViewModel = queueViewModel;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DialogFragmentQueueOrderToProcessBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        rvQueueOrderList = binding.rvQueueOrderToProcessList;
        btnQueueOrderDone = binding.btnQueueOrderToProcessDone;
        btnQueueOrderReady = binding.btnQueueOrderToProcessReady;
        btnQueueOrderClose = binding.btnQueueOrderToProcessClose;

        btnQueueOrderDone.setEnabled(false);
        btnQueueOrderReady.setEnabled(false);

        btnQueueOrderReady.setVisibility(View.GONE);
        btnQueueOrderDone.setVisibility(View.GONE);

        tvQueueOrderAmount = binding.tvQueueOrderToDoneAmount;
        tvQueueOrderCustomerName = binding.tvQueueOrderCustomerName;

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        root.setLayoutParams(params);

        queueOrderToProcessAdapter = new QueueOrderToProcessAdapter();

        rvQueueOrderList.setHasFixedSize(true);
        rvQueueOrderList.setLayoutManager(new LinearLayoutManager(getContext()));

        rvQueueOrderList.setAdapter(queueOrderToProcessAdapter);
        queueViewModel.queueOrderToProcess.observe(getViewLifecycleOwner(), queueOrder -> {
            if(queueOrder == null) {
                dismiss();
                return;
            };
            /*if(queueOrder.getStatus() == OrderStatus.PENDING)
                btnQueueOrderReady.setVisibility(View.VISIBLE);
            if(queueOrder.getStatus() == OrderStatus.READY)
                btnQueueOrderDone.setVisibility(View.VISIBLE);
            String price = formatter.formatAsPHP(queueOrder.getTotalPayment());
            tvQueueOrderCustomerName.setText(queueOrder.getCustomerName());
            tvQueueOrderAmount.setText(price);
        });*/
            // Check the order status and manage button visibility accordingly
            if (queueOrder.getStatus() == OrderStatus.PENDING) {
                // Show the "Ready" button and hide the "Done" button
                btnQueueOrderReady.setVisibility(View.VISIBLE);
                btnQueueOrderDone.setVisibility(View.GONE);
            } else {
                // Hide the "Ready" button and show the "Done" button
                btnQueueOrderReady.setVisibility(View.GONE);
                btnQueueOrderDone.setVisibility(View.VISIBLE);
            }
        });

        queueViewModel.cartItemList.observe(getViewLifecycleOwner(), cartItems -> {
            if(cartItems == null) return;
            this.cartItems = cartItems;
            queueOrderToProcessAdapter.UpdateList(cartItems);
            btnQueueOrderDone.setEnabled(true);
            btnQueueOrderReady.setEnabled(true);
        });

        btnQueueOrderReady.setOnClickListener(view -> {
            queueViewModel.readyQueueOrder(cartItems);
            btnQueueOrderReady.setEnabled(false);
        });
        btnQueueOrderDone.setOnClickListener(view -> {
            queueViewModel.finishQueueOrder();
            btnQueueOrderDone.setEnabled(false);
        });

        btnQueueOrderClose.setOnClickListener(view -> {
            queueViewModel.clearQueueOrderToProcess();
            dismiss();
        });

        return root;
    }

    ArrayList<CartItem> cartItems;

    @Override
    public void onDestroyView() {
        queueViewModel.clearQueueOrderToProcess();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        queueViewModel.clearQueueOrderToProcess();
        super.onDetach();
    }
}
