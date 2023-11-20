package com.example.coftea.Cashier.queue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.coftea.Order.OrderDialogFragment;
import com.example.coftea.data.OrderStatus;
import com.example.coftea.databinding.FragmentQueueBinding;

public class QueueFragment extends Fragment {

    private FragmentQueueBinding binding;
    QueueViewModel queueViewModel;
    private RecyclerView rvCashierQueueOrderList;
    private QueueOrderToProcessDialogFragment queueOrderToProcessDialogFragment;
    private QueueOrderAdapter queueOrderAdapter;
    private Button btnQueueOrderPending, btnQueueOrderReady, btnQueueOrderDone, btnQueueOrderCancelled;
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

        btnQueueOrderCancelled = binding.btnQueueOrderCancelled;
        btnQueueOrderPending = binding.btnQueueOrderPending;
        btnQueueOrderReady = binding.btnQueueOrderReady;
        btnQueueOrderDone = binding.btnQueueOrderDone;

        btnQueueOrderPending.setEnabled(false);

        queueViewModel = new ViewModelProvider(this, new QueueViewModelFactory(OrderStatus.PENDING)).get(QueueViewModel.class);

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

    private void updateButtonState(Button clickedButton) {
        btnQueueOrderPending.setEnabled(true);
        btnQueueOrderReady.setEnabled(true);
        btnQueueOrderDone.setEnabled(true);
        btnQueueOrderCancelled.setEnabled(true);

        clickedButton.setEnabled(false);
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
                queueOrderToProcessDialogFragment.show(getParentFragmentManager(), "QueueOrderToDone");
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
