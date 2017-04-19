package ar.uba.fi.tdp2.trips.AttractionDetails;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import ar.uba.fi.tdp2.trips.Multimedia.GalleryFragment;

import ar.uba.fi.tdp2.trips.TourDetails.TourDetailsFragment;

public class Pager extends FragmentStatePagerAdapter {
    private int tabCount;
    private int attractionId;

    private static final int NO_POINT_OF_INTEREST = -1;

    public Pager(FragmentManager fm, int tabCount, int attractionId) {
        super(fm);
        this.tabCount = tabCount;
        this.attractionId = attractionId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return AttractionDetailsFragment.newInstance(attractionId);
            case 1: return GalleryFragment.newInstance(attractionId, NO_POINT_OF_INTEREST);
            case 2: return PointOfInterestFragment.newInstance(attractionId);
        };
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}

