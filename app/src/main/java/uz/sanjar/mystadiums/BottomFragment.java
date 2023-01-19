package uz.sanjar.mystadiums;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import uz.sanjar.mystadiums.databinding.CustomDialogBinding;

public class BottomFragment extends BottomSheetDialogFragment {

    private CustomDialogBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CustomDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingData();
    }


    private void loadingData() {
        assert getArguments() != null;
        binding.addressLine.setText(getArguments().getString("getAddressLine"));
        binding.countryName.setText(getArguments().getString("getCountryName"));
        binding.adminArea.setText(getArguments().getString("getAdminArea"));
        binding.subAdminArea.setText(getArguments().getString("getSubAdminArea"));
        binding.locality.setText(getArguments().getString("getLocality"));
        binding.countryCode.setText(getArguments().getString("getCountryCode"));
        binding.latLng.setText(getArguments().getString("getLatLng"));
    }
}
