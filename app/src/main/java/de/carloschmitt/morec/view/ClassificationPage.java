package de.carloschmitt.morec.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import de.carloschmitt.morec.databinding.PageClassificationBinding;
import de.carloschmitt.morec.viewmodel.ClassificationPageViewModel;


public class ClassificationPage extends Fragment {
    Context context;
    PageClassificationBinding binding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = PageClassificationBinding.inflate(inflater, container, false);
        ClassificationPageViewModel classificationPageViewModel = new ViewModelProvider(getActivity()).get(ClassificationPageViewModel.class);
        binding.setClassificationPageViewModel(classificationPageViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        context = binding.getRoot().getContext();
        return binding.getRoot();
    }
}
