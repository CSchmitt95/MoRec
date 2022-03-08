package de.carloschmitt.morec.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import de.carloschmitt.morec.adapters.LabelAdapter;
import de.carloschmitt.morec.databinding.PageRecordBinding;
import de.carloschmitt.morec.viewmodel.LabelPageViewModel;

public class LabelPage extends Fragment {
    private static final String TAG = "LabelPage";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PageRecordBinding binding = PageRecordBinding.inflate(inflater);

        LabelPageViewModel labelPageViewModel = new ViewModelProvider(getActivity()).get(LabelPageViewModel.class);
        binding.setLabelPageViewModel(labelPageViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        //RecylcerView
        binding.list.setLayoutManager(new LinearLayoutManager(getContext()));
        LabelAdapter labelAdapter = new LabelAdapter(labelPageViewModel);
        labelPageViewModel.getLabelList().observe(getViewLifecycleOwner(), list -> labelAdapter.submitList(new ArrayList<>(list)));
        binding.list.setAdapter(labelAdapter);

        return binding.getRoot();
    }

}
