package com.laurensius.simlakalantas.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laurensius.simlakalantas.R;

public class FragmentPelaporBeranda extends Fragment {

    public FragmentPelaporBeranda() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View inflaterBeranda = inflater.inflate(R.layout.fragment_pelapor_beranda, container, false);
        return inflaterBeranda;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
