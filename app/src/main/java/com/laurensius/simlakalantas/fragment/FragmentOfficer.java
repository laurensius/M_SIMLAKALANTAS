package com.laurensius.simlakalantas.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.laurensius.simlakalantas.AppOfficer;
import com.laurensius.simlakalantas.R;

public class FragmentOfficer extends Fragment {

    LinearLayout llOfficerLaporanMasuk;
    LinearLayout llOfficerLaporanProses;
    LinearLayout llOfficerRiwayatLaporan;
    LinearLayout llOfficerPemberitahuan;
    LinearLayout llOfficerProfil;
    LinearLayout llOfficerKeluar;

    public FragmentOfficer() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View inflaterBeranda = inflater.inflate(R.layout.fragment_officer, container, false);
        llOfficerLaporanMasuk = (LinearLayout)inflaterBeranda.findViewById(R.id.officer_laporan_masuk);
        llOfficerLaporanProses = (LinearLayout)inflaterBeranda.findViewById(R.id.officer_laporan_dalam_proses);
        llOfficerRiwayatLaporan = (LinearLayout)inflaterBeranda.findViewById(R.id.officer_riwayat_laporan);
        llOfficerPemberitahuan = (LinearLayout)inflaterBeranda.findViewById(R.id.officer_pemberitahuan);
        llOfficerProfil= (LinearLayout)inflaterBeranda.findViewById(R.id.officer_profil);
        llOfficerKeluar = (LinearLayout)inflaterBeranda.findViewById(R.id.officer_keluar);
        return inflaterBeranda;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        llOfficerLaporanMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppOfficer.stage = getResources().getString(R.string.stage_waiting);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fl_officer, new FragmentLaporanOfficer());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        llOfficerLaporanProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppOfficer.stage = getResources().getString(R.string.stage_process);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fl_officer, new FragmentLaporanOfficer());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        llOfficerRiwayatLaporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppOfficer.stage = getResources().getString(R.string.stage_finish);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fl_officer, new FragmentLaporanOfficer());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        llOfficerPemberitahuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fl_officer, new FragmentPemberitahuan());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        llOfficerProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fl_officer, new FragmentProfil());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
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
