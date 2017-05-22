package ar.uba.fi.tdp2.trips.Notifications;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeUnit;

import ar.uba.fi.tdp2.trips.Common.Utils;
import ar.uba.fi.tdp2.trips.R;

public class RV_NotificationsAdapter extends RecyclerView.Adapter<RV_NotificationsAdapter.NotificationViewHolder> {

    List<Notification> notifications;
    Context actualContext;

    public RV_NotificationsAdapter(List<Notification> notifications, Context context) {
        this.notifications  = notifications;
        this.actualContext  = context;
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView notificationTitle;
        TextView notificationMsg;
        TextView notificationDate;
        ImageView urlHyperlink;

        NotificationViewHolder(View itemView) {
            super(itemView);
            cardView          = (CardView)  itemView.findViewById(R.id.notification_card);
            notificationTitle = (TextView)  itemView.findViewById(R.id.notification_title);
            notificationMsg   = (TextView)  itemView.findViewById(R.id.notification_msg);
            notificationDate  = (TextView)  itemView.findViewById(R.id.notification_date);
            urlHyperlink      = (ImageView) itemView.findViewById(R.id.url_hyperlink);
        }
    }
    @Override
    public RV_NotificationsAdapter.NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_card, parent, false);
        return new RV_NotificationsAdapter.NotificationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RV_NotificationsAdapter.NotificationViewHolder holder, int position) {
        final Notification notification = notifications.get(position);
        holder.notificationTitle.setText(notification.title);
        holder.notificationMsg.setText(notification.message);
        holder.notificationDate.setText(getNotificationDate(notification.dateInSeconds, actualContext));

        final boolean urlIsNotBlank = Utils.isNotBlank(notification.url);
        holder.urlHyperlink.setVisibility((urlIsNotBlank) ? View.VISIBLE : View.GONE);
        holder.urlHyperlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = notification.url;
                if (urlIsNotBlank) {
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://" + url;
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    if (intent.resolveActivity(actualContext.getPackageManager()) != null) {
                        actualContext.startActivity(intent);
                    }
                }
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!urlIsNotBlank) {
                    Toast.makeText(actualContext, actualContext.getString(R.string.no_url_in_notification), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private static String getNotificationDate(long dateInSeconds, Context actualContext) {
        //Obtengo el epoch actual y la diferencia con el de la notificación
        long myEpoch = System.currentTimeMillis() / 1000;
        long diff = Math.abs(myEpoch - dateInSeconds);
        StringBuilder builder = new StringBuilder();
        String unit;

        long days = TimeUnit.SECONDS.toDays(diff);

        if (days > 0) {
            long years = days / 365;
            long months = days / 30;
            //Ver si son Años, meses o dias
            if (years > 0) {
                unit = (years != 1) ? actualContext.getString(R.string.years_unit) : actualContext.getString(R.string.year_unit);
                builder.append(actualContext.getString(R.string.ago, years, unit));
            } else if (months > 0) {
                unit = (months != 1) ? actualContext.getString(R.string.months_unit) : actualContext.getString(R.string.month_unit);
                builder.append(actualContext.getString(R.string.ago, months, unit));
            } else {
                unit = (days != 1) ? actualContext.getString(R.string.days_unit) : actualContext.getString(R.string.day_unit);
                builder.append(actualContext.getString(R.string.ago, days, unit));
            }
        } else {
            long hours = TimeUnit.SECONDS.toHours(diff);
            long minutes = TimeUnit.SECONDS.toMinutes(diff);
            //Ver si son horas o minutos
            if (hours > 0) {
                unit = (hours != 1) ? actualContext.getString(R.string.hours_unit) : actualContext.getString(R.string.hour_unit);
                builder.append(actualContext.getString(R.string.ago, hours, unit));
            } else if (minutes > 0) {
                unit = (minutes != 1) ? actualContext.getString(R.string.minutesUnit) : actualContext.getString(R.string.minute_unit);
                builder.append(actualContext.getString(R.string.ago, minutes, unit));
            } else {
                builder.append(actualContext.getString(R.string.just_now));
            }
        }

        return builder.toString();
    }

}
