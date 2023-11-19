package com.example.coftea.Cashier.queue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.coftea.Customer.advance_order.CustomerAdvancedOrderAdapter;
import com.example.coftea.Order.OrderDialogFragment;
import com.example.coftea.databinding.FragmentQueueBinding;

import java.util.ArrayList;

public class QueueFragment extends Fragment {

    private FragmentQueueBinding binding;
    QueueViewModel queueViewModel;
    private RecyclerView rvCashierQueueOrderList;
    private QueueOrderToDoneDialogFragment queueOrderToDoneDialogFragment;
    private QueueOrderAdapter queueOrderAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentQueueBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        init();
        listen();

        return root;
    }

    private void init(){
        rvCashierQueueOrderList = binding.rvCashierQueueOrderList;
        queueViewModel = new ViewModelProvider(this).get(QueueViewModel.class);

        rvCashierQueueOrderList = binding.rvCashierQueueOrderList;

        rvCashierQueueOrderList.setHasFixedSize(true);
        rvCashierQueueOrderList.setLayoutManager(new LinearLayoutManager(getContext()));

        queueOrderToDoneDialogFragment = new QueueOrderToDoneDialogFragment(queueViewModel);
        queueOrderAdapter = new QueueOrderAdapter(queueViewModel);
        rvCashierQueueOrderList.setAdapter(queueOrderAdapter);
    }

    private void listen(){
        queueViewModel.queueOrderList.observe(getViewLifecycleOwner(), queueOrders -> {
            if(queueOrders == null) return;
            queueOrderAdapter.UpdateList(queueOrders);
        });

        queueViewModel.queueOrderToDone.observe(getViewLifecycleOwner(), orderItem -> {
            if (orderItem == null) return;
            OrderDialogFragment existingFragment = (OrderDialogFragment) getParentFragmentManager().findFragmentByTag("QueueOrderToDone");

            if (existingFragment == null)
                queueOrderToDoneDialogFragment.show(getParentFragmentManager(), "QueueOrderToDone");
            else
                existingFragment.getDialog().show();
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
