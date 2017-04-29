package ar.uba.fi.tdp2.trips.PointsOfInterest;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ar.uba.fi.tdp2.trips.Multimedia.GalleryFragment;

public class PointOfInterestDetailsPager extends FragmentStatePagerAdapter {
    private int tabCount;
    private int attractionId;
    private int poiId;

    public PointOfInterestDetailsPager(FragmentManager fm, int tabCount, int attractionId, int poiId) {
        super(fm);
        this.tabCount = tabCount;
        this.attractionId = attractionId;
        this.poiId = poiId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return PointOfInterestDetailsFragment.newInstance(attractionId, poiId);
            case 1: return GalleryFragment.newInstance(attractionId, poiId);
        };
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
