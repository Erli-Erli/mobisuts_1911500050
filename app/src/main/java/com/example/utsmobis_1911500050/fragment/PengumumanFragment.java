package com.example.utsmobis_1911500050.fragment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.utsmobis_1911500050.ClassGlobal;
import com.example.utsmobis_1911500050.MainActivity;
import com.example.utsmobis_1911500050.R;
import com.example.utsmobis_1911500050.adapter.PengumumanAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PengumumanFragment extends Fragment {

    private PengumumanViewModel mViewModel;
    SwipeRefreshLayout swipe;
    RecyclerView rvPromo;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
    PengumumanAdapter adapter;

    public static PengumumanFragment newInstance() {
        return new PengumumanFragment();
    }
    private void konekDB(String aksi){
        stringRequest = new StringRequest(Request.Method.POST,
                ((ClassGlobal) getActivity().getApplication()).getURL() + "pengumuman.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            String pesan = jObj.getString("pesan");
                            boolean hasil = jObj.getBoolean("hasil");
                            if(!hasil)
                                Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();
                            else if(hasil) {
                                if(aksi.equalsIgnoreCase("cekpengumuman")){
                                    JSONArray jsonArray = jObj.getJSONArray("resCekPromo");
                                    for (int a = 0; a < jsonArray.length(); a++) {
                                        JSONObject json = jsonArray.getJSONObject(a);
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map.put("judul", json.getString("jdlPengumuman"));
                                        map.put("tglPost", json.getString("datePosting"));
                                        map.put("desc", json.getString("deskripsi"));
                                        map.put("cover", json.getString("coverPengumuman"));
                                        arrayList.add(map);
                                        adapter = new PengumumanAdapter((MainActivity) getActivity(), arrayList);
                                        rvPromo.setAdapter(adapter);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } //akhir onResponse
                }, //akhir Listener
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Gagal menghubungi server : " +
                                error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) //akhir ErrorListener dan new StringRequest
        {   @Override
        protected Map<String,String> getParams() throws AuthFailureError {
            Map<String, String> param = new HashMap<String, String>();
            if(aksi.equalsIgnoreCase("cekpengumuman"))
                param.put("aksi","cekpengumuman");
            return param;
        }
        }; //akhir stringRequest =
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }//akhir method konekDB
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pengumuman, container, false);

        rvPromo = (RecyclerView) view.findViewById(R.id.rvPengumuman);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvPromo.setLayoutManager(llm);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);

        konekDB("cekpengumuman");
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(true);
                PengumumanFragment pengumumanFragment = new PengumumanFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_content_main, pengumumanFragment, "promo1")
                        //.addToBackStack(null) //agar dapat ke fragment sebelumnya dengan BACK
                        .commit();
                swipe.setRefreshing(false);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PengumumanViewModel.class);
        // TODO: Use the ViewModel
    }

}