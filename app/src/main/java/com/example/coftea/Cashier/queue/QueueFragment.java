package com.example.coftea.Cashier.queue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.coftea.databinding.FragmentQueueBinding;

public class QueueFragment extends Fragment {

    private FragmentQueueBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        QueueViewModel queueViewModel =
                new ViewModelProvider(this).get(QueueViewModel.class);

        binding = FragmentQueueBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textQueue;
        queueViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
