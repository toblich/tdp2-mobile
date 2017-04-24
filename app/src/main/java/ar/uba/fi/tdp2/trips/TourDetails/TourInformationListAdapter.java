package ar.uba.fi.tdp2.trips.TourDetails;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ar.uba.fi.tdp2.trips.Attraction;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.Tour;
import ar.uba.fi.tdp2.trips.Utils;

public class TourInformationListAdapter extends BaseAdapter {
    private List<InfoItem> items;
    private Context context;
    private Tour tour;

    public class InfoItem {
        String value;
        int iconId;
//        OnClickCallback callback;

        public InfoItem(int iconId, String value) {
            this.value = value;
            this.iconId = iconId;
        }
    }

    public TourInformationListAdapter(Context context, Tour tour) {
        this.context = context;
        this.tour = tour;
        this.items = new ArrayList<>();
        loadInfoItems();
    }

    private void loadInfoItems() {
        if (tour.getDuration() > 0) { // TODO make check here more robust
            items.add(new InfoItem(R.drawable.ic_timer_black_24dp, Utils.prettyTimeStr(tour.getDuration())));
        }

        if (tour.getAvgRating() > 0) {
            items.add(new InfoItem(R.drawable.ic_stars_black_24dp, String.format(Locale.getDefault(),
                    "%.1f %s 5.0", tour.getAvgRating(), context.getString(R.string.out_of))));
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        final View infoItem = inflater.inflate(R.layout.attraction_details_info_item, parent, false);
        final TextView value = (TextView) infoItem.findViewById(R.id.value);
        final ImageView icon = (ImageView) infoItem.findViewById(R.id.icon);

        final InfoItem item = items.get(position);

        value.setText(item.value);
        icon.setImageResource(item.iconId);

//        if (item.callback != null) {
//            infoItem.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    TextView hours = (TextView) infoItem.findViewById(R.id.hours);
//                    item.callback.call(value, hours);
//                }
//            });
//        }

        return infoItem;
    }
}
