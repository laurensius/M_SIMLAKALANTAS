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

import com.laurensius.simlakalantas.R;

public class FragmentOfficer extends Fragment {

    LinearLayout llPelaporFormLaporan;
    LinearLayout llPelaporRiwayatLaporan;
    LinearLayout llPelaporSebaranPolsek;
    LinearLayout llPelaporPemberitahuan;
    LinearLayout llPelaporProfil;
    LinearLayout llPelaporKeluar;

    public FragmentOfficer() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View inflaterBeranda = inflater.inflate(R.layout.fragment_pelapor, container, false);
        llPelaporFormLaporan = (LinearLayout)inflaterBeranda.findViewById(R.id.pelapor_form_laporan);
        llPelaporRiwayatLaporan = (LinearLayout)inflaterBeranda.findViewById(R.id.pelapor_riwayat_laporan);
        llPelaporSebaranPolsek = (LinearLayout)inflaterBeranda.findViewById(R.id.pelapor_sebaran_polsek);
        llPelaporPemberitahuan = (LinearLayout)inflaterBeranda.findViewById(R.id.pelapor_pemberitahuan);
        llPelaporProfil= (LinearLayout)inflaterBeranda.findViewById(R.id.pelapor_profil);
        llPelaporKeluar = (LinearLayout)inflaterBeranda.findViewById(R.id.pelapor_keluar);
        return inflaterBeranda;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        llPelaporFormLaporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fl_pelapor, new FragmentFormPelaporan());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        llPelaporRiwayatLaporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fl_pelapor, new FragmentRiwayatLaporan());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        llPelaporSebaranPolsek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fl_pelapor, new FragmentPetaSebaranPolsek());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        llPelaporPemberitahuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fl_pelapor, new FragmentPemberitahuan());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        llPelaporProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fl_pelapor, new FragmentProfil());
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
