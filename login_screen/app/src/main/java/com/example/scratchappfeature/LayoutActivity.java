package com.example.scratchappfeature;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);

        Button layoutOneBtn = findViewById(R.id.layoutActivityBtnLayoutOne);
        Button layoutTwoBtn = findViewById(R.id.layoutActivityBtnLayoutTwo);
        Button layoutThreeBtn = findViewById(R.id.layoutActivityBtnLayoutThree);
        Button layoutFourBtn = findViewById(R.id.layoutActivityBtnLayoutFour);
        Button layoutFiveBtn = findViewById(R.id.layoutActivityBtnLayoutFive);
        Button layoutSixBtn = findViewById(R.id.layoutActivityBtnLayoutSix);

        FragmentManager fragmentManager = getSupportFragmentManager();

        layoutOneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateFragment(fragmentManager, R.layout.fragment_layout_one);
            }
        });

        layoutTwoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateFragment(fragmentManager, R.layout.fragment_layout_two);
            }
        });

        layoutThreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateFragment(fragmentManager, R.layout.fragment_layout_three);
            }
        });

        layoutFourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateFragment(fragmentManager, R.layout.fragment_layout_four);
            }
        });


        layoutFiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateFragment(fragmentManager, R.layout.fragment_layout_five);
            }
        });
    }

    private void inflateFragment(FragmentManager fragmentManager, int layoutId){
        LayoutFragment layoutOne = LayoutFragment.newInstance(layoutId);
        fragmentManager.beginTransaction()
                .replace(R.id.layoutFragmentContainerView, layoutOne)
                .setReorderingAllowed(true)
                .addToBackStack("name")
                .commit();
    }
}
