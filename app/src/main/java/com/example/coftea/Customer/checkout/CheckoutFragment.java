package com.example.coftea.Customer.checkout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.example.coftea.Paymongo.PaymongoCheckout;
import com.example.coftea.Paymongo.PaymongoCheckoutListener;
import com.example.coftea.Paymongo.PaymongoResponse;
import com.example.coftea.R;
import com.example.coftea.databinding.FragmentAdvanceOrderBinding;
import com.example.coftea.databinding.FragmentCheckoutBinding;

public class CheckoutFragment extends Fragment implements PaymongoCheckoutListener {

    private FragmentCheckoutBinding binding;
    private TextView tvTotalAmountDue;
    private Button btnPayWithGCash;
    CheckoutViewModel checkoutViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        init();

        return root;
    }

    private void init(){
        checkoutViewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);
        tvTotalAmountDue = binding.tvTotalAmountDue;
        btnPayWithGCash = binding.btnPayWithGCash;

        btnPayWithGCash.setOnClickListener(view -> {
            startCheckout();
        });
    }
    private void startCheckout(){
        new PaymongoCheckout(this).execute();
    }

    @Override
    public void onPaymongoCheckoutComplete(PaymongoResponse result) {
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getCheckoutUrl()));
            startActivity(intent);
        }
        catch (Exception e){
            Log.e("ERROR", e.toString());
        }

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
