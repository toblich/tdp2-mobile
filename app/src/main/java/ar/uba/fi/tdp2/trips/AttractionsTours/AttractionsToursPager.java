package ar.uba.fi.tdp2.trips.AttractionsTours;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ar.uba.fi.tdp2.trips.AttractionsTours.Attractions.AttractionsListFragment;
import ar.uba.fi.tdp2.trips.AttractionsTours.Tours.ToursListFragment;

public class AttractionsToursPager extends FragmentStatePagerAdapter {
    private int tabCount;
    private Double latitude;
    private Double longitude;
    private int cityId;
    private AttractionsListFragment attractionsListFragment;
    private ToursListFragment toursListFragment;

    public AttractionsToursPager(FragmentManager fragmentManager, int tabCount, Double latitude, Double longitude, int cityId) {
        super(fragmentManager);
        this.tabCount = tabCount;
        this.cityId = cityId;
        this.latitude = latitude;
        this.longitude = longitude;

        this.attractionsListFragment = AttractionsListFragment.newInstance(latitude, longitude, cityId);
        this.toursListFragment = ToursListFragment.newInstance(cityId);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return this.attractionsListFragment;
            case 1: return this.toursListFragment;
        };
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    public void setFilter(String filter) {
        this.attractionsListFragment.setFilter(filter);
        this.toursListFragment.setFilter(filter);
    }
}

