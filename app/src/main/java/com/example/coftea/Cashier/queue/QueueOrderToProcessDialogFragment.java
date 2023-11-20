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

import com.example.coftea.databinding.DialogFragmentQueueOrderToProcessBinding;
import com.example.coftea.utilities.PHPCurrencyFormatter;


public class QueueOrderToProcessDialogFragment extends DialogFragment {
    PHPCurrencyFormatter formatter = PHPCurrencyFormatter.getInstance();
    DialogFragmentQueueOrderToProcessBinding binding;
    QueueOrderToProcessAdapter queueOrderToProcessAdapter;
    RecyclerView rvQueueOrderList;
    Button btnQueueOrderReady, btnQueueOrderDone, btnQueueOrderCancel, btnQueueOrderClose;
    TextView tvQueueOrderCustomerName, tvQueueOrderAmount;
    QueueViewModel queueViewModel;

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
        btnQueueOrderCancel = binding.btnQueueOrderToProcessCancel;
        btnQueueOrderClose = binding.btnQueueOrderToProcessClose;

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

        queueViewModel.queueOrderToDone.observe(getViewLifecycleOwner(), queueOrder -> {
            if(queueOrder == null) return;
            String price = formatter.formatAsPHP(queueOrder.getTotalPayment());
            tvQueueOrderCustomerName.setText(queueOrder.getCustomerName());
            tvQueueOrderAmount.setText(price);
        });

        queueViewModel.cartItemList.observe(getViewLifecycleOwner(), cartItems -> {
            if(cartItems == null) return;
            queueOrderToProcessAdapter.UpdateList(cartItems);
        });

        btnQueueOrderReady.setOnClickListener(view -> {
//            queueViewModel.setQueueOrderToProcess();
        });

        btnQueueOrderClose.setOnClickListener(view -> {
            queueViewModel.clearQueueOrderToProcess();
            dismiss();
        });

        return root;
    }

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
