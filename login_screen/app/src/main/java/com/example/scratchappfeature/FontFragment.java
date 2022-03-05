package com.example.scratchappfeature;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FontFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FontFragment extends Fragment {

    ArrayList<String> listOfFonts = new ArrayList<String>() {
        {
            add("sans-serif");
            add("sans-serif-thin");
            add("sans-serif-light");
            add("sans-serif-medium");
            add("sans-serif-black");
            add("sans-serif-condensed");
            add("sans-serif-condensed-light");
            add("sans-serif-condensed-medium");
            add("serif");
            add("monospace");
            add("serif-monospace");
            add("casual");
            add("cursive");
            add("sans-serif-smallcaps");
        }
    };

    String fontSelection;
    ItemViewModel viewModel;




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FontFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FontFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FontFragment newInstance(String fontSelection) {
        FontFragment fragment = new FontFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, fontSelection);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.fontSelection = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_font, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        populateRecyclyerView(view, viewModel);
        return view;
    }

    private void populateRecyclyerView(View view, ItemViewModel viewModel){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.fontRecyclerView);
        recyclerView.setLayoutManager((linearLayoutManager));
        FontRVAdapter adapter = new FontRVAdapter(getContext(), listOfFonts);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new FontRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                fontSelection = listOfFonts.get(position);
                viewModel.setData(fontSelection);
            }
        });
    }


}