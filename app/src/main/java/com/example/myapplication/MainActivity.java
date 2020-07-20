package com.example.myapplication;

import android.Manifest;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

//PROMEMORIA
//ricordare che nei file xml sono da cancellare activity_accelerometro (che sarebbe il vecchio layout gestione sensori),
//activity_lista_sensori_movimento, activity_lista_sensori_posizione, activity_lista_sensori_ambiente
//NEL PACKAGE VECCHIECLASSI TROVI LE VECCHIE ACTIVITY UTILIZZATE

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tabs);//tabBar nel video

        final ViewPager viewPager = findViewById(R.id.viewPager);
        PageAdapter pagerAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
    }//fine onCreate();

    private class PageAdapter extends FragmentPagerAdapter {
        private int numOfTabs;

        public PageAdapter(FragmentManager fm, int numOfTabs) {
            super(fm, numOfTabs);
            this.numOfTabs = numOfTabs;
        }


        // Will be displayed as the tab's label
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0:
                    return getResources().getString(R.string.Movimento);

                case 1:
                    return getResources().getString(R.string.Posizione);

                case 2:
                    return getResources().getString(R.string.Ambiente);

                default:
                    return null;
            }
        }
        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0:
                    return new Fragment_listaMovimento();
                case 1:
                    return new Fragment_listaPosizione();
                case 2:
                    return new Fragment_listaAmbiente();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return numOfTabs;
        }
    }
}