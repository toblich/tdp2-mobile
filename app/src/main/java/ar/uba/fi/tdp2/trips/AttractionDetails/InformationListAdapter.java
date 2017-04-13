package ar.uba.fi.tdp2.trips.AttractionDetails;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.uba.fi.tdp2.trips.Attraction;
import ar.uba.fi.tdp2.trips.R;

public class InformationListAdapter extends BaseAdapter {
    public class InfoItem {
        String value;
        int iconId;
        AttractionDetailsFragment.OnClickCallback callback;

        public InfoItem(String value, int iconId, AttractionDetailsFragment.OnClickCallback callback) {
            this.value = value;
            this.iconId = iconId;
            this.callback = callback;
        }
    }

    private Context context;
    private Attraction attraction;
    private List<InfoItem> items;

    public InformationListAdapter(Context context, Attraction attraction) {
        this.context = context;
        this.attraction = attraction;
        this.items = new ArrayList<>();
        loadItems();
    }

    private void loadItems() {
        add(R.drawable.ic_place_black_24dp, attraction.address);
        add(R.drawable.ic_public_black_24dp, attraction.url);
        add(R.drawable.ic_phone_black_24dp, attraction.phone);
        add(R.drawable.ic_access_time_black_24dp, attraction.openingHours);
        add(R.drawable.ic_attach_money_black_24dp, attraction.price, context.getString(R.string.dollars));
        add(R.drawable.ic_timer_black_24dp, attraction.duration, context.getString(R.string.minutes));
    }

    private void add(int iconId, String string) {
        if (string != null && !string.equals("")) {
            items.add(new InfoItem(string, iconId, null));
        }
    }

    private void add(int iconId, Number num, String string) {
        if (num != null) { // TODO make string pretty
            items.add(new InfoItem(num.toString() + " " + string, iconId, null));
        }
    }

    private void add(int iconId, final List<Attraction.OpeningHour> openingHours) {
        if (openingHours == null || openingHours.isEmpty()) {
            return;
        }

        AttractionDetailsFragment.OnClickCallback callback = openingHours.size() == 1 ? null : new AttractionDetailsFragment.OnClickCallback() {
            @Override
            public void call(View view, TextView days, TextView hours, ImageView icon) {
                // TODO
                System.out.println("ON CLICK CALLBACK");
                StringBuilder daysBuilder = new StringBuilder();
                StringBuilder hoursBuilder = new StringBuilder();
                for (Attraction.OpeningHour op: openingHours) {
                    daysBuilder.append(op.day + '\n');
                    hoursBuilder.append("    " + (op.start == null ? context.getString(R.string.all_day_open) : op.start + " - " + op.end) + '\n');
                }
                days.setText(daysBuilder.toString());
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.RIGHT_OF, icon.getId());
                days.setLayoutParams(layoutParams);
                hours.setVisibility(View.VISIBLE);
                hours.setText(hoursBuilder.toString());
            }
        };

        Attraction.OpeningHour first = openingHours.get(0);
        String initial = first.day + "    " + (first.start == null ? context.getString(R.string.all_day_open) : (first.start + " - " + first.end));
        items.add(new InfoItem(initial, iconId, callback));
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        final View infoItem = inflater.inflate(R.layout.attraction_info_item, parent, false);
        final TextView value = (TextView) infoItem.findViewById(R.id.value);
        final ImageView icon = (ImageView) infoItem.findViewById(R.id.icon);

        final InfoItem item = items.get(position);

        value.setText(item.value);
        icon.setImageResource(item.iconId);

        if (item.callback != null) {
            value.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView hours = (TextView) infoItem.findViewById(R.id.hours);
                    item.callback.call(infoItem, value, hours, icon);
                }
            });
        }

        return infoItem;
    }
}
