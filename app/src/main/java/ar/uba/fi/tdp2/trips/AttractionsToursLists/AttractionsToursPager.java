package ar.uba.fi.tdp2.trips.AttractionsToursLists;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class AttractionsToursPager extends FragmentStatePagerAdapter {
    private int tabCount;
    private Double latitude;
    private Double longitude;
    private int cityId;

    public AttractionsToursPager(FragmentManager fragmentManager, int tabCount, Double latitude, Double longitude, int cityId) {
        super(fragmentManager);
        this.tabCount = tabCount;
        this.cityId = cityId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return AttractionsListFragment.newInstance(latitude, longitude, cityId);
            case 1: return ToursListFragment.newInstance(cityId);
        };
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}

