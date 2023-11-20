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

import com.example.coftea.databinding.DialogFragmentQueueOrderToDoneBinding;
import com.example.coftea.utilities.PHPCurrencyFormatter;


public class QueueOrderToProcessDialogFragment extends DialogFragment {
    PHPCurrencyFormatter formatter = PHPCurrencyFormatter.getInstance();
    DialogFragmentQueueOrderToDoneBinding binding;
    QueueOrderToProcessAdapter queueOrderToProcessAdapter;
    RecyclerView rvQueueOrderList;
    Button btnQueueOrderConfirm, btnQueueOrderClose;
    TextView tvQueueOrderCustomerName, tvQueueOrderAmount;
    QueueViewModel queueViewModel;

    public QueueOrderToProcessDialogFragment(QueueViewModel queueViewModel){
        this.queueViewModel = queueViewModel;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DialogFragmentQueueOrderToDoneBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        rvQueueOrderList = binding.rvQueueOrderToDoneList;
        btnQueueOrderConfirm = binding.btnQueueOrderToDoneConfirm;
        btnQueueOrderClose = binding.btnQueueOrderToDoneClose;

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

        btnQueueOrderConfirm.setOnClickListener(view -> {
            queueViewModel.clearQueueOrderToDone();
        });

        btnQueueOrderClose.setOnClickListener(view -> {
            queueViewModel.clearQueueOrderToDone();
            dismiss();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        queueViewModel.clearQueueOrderToDone();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        queueViewModel.clearQueueOrderToDone();
        super.onDetach();
    }
}
