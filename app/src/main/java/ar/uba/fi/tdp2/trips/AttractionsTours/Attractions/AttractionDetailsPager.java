package ar.uba.fi.tdp2.trips.AttractionsTours.Attractions;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import ar.uba.fi.tdp2.trips.Multimedia.GalleryFragment;

import ar.uba.fi.tdp2.trips.Common.Utils;
import ar.uba.fi.tdp2.trips.PointsOfInterest.PointOfInterestFragment;

public class AttractionDetailsPager extends FragmentStatePagerAdapter {
    private int tabCount;
    private int attractionId;

    public AttractionDetailsPager(FragmentManager fm, int tabCount, int attractionId) {
        super(fm);
        this.tabCount = tabCount;
        this.attractionId = attractionId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return AttractionDetailsFragment.newInstance(attractionId);
            case 1: return GalleryFragment.newInstance(attractionId, Utils.NO_POINT_OF_INTEREST);
            case 2: return PointOfInterestFragment.newInstance(attractionId);
        };
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}

