package com.laurensius.simlakalantas.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.laurensius.simlakalantas.AppOfficer;
import com.laurensius.simlakalantas.Login;
import com.laurensius.simlakalantas.R;
import com.laurensius.simlakalantas.ServiceNotification;

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
        llOfficerKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(getResources().getString(R.string.confirm_logout_title))
                        .setMessage(getResources().getString(R.string.confirm_logout_body))
                        .setIcon(android.R.drawable.ic_menu_help)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                getActivity().stopService(new Intent(getActivity().getBaseContext(), ServiceNotification.class));
                                SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedpreferences), 0);
                                SharedPreferences.Editor editorPreferences = sharedPreferences.edit();
                                editorPreferences.clear();
                                editorPreferences.commit();
                                Intent i = new Intent(getActivity(), Login.class);
                                startActivity(i);
                                getActivity().finish();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
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
