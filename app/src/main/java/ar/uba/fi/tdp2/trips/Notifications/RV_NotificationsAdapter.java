package ar.uba.fi.tdp2.trips.Notifications;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

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

        NotificationViewHolder(View itemView) {
            super(itemView);
            cardView          = (CardView) itemView.findViewById(R.id.notification_card);
            notificationTitle = (TextView) itemView.findViewById(R.id.notification_title);
            notificationMsg   = (TextView) itemView.findViewById(R.id.notification_msg);
            notificationDate  = (TextView) itemView.findViewById(R.id.notification_date);
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
        holder.notificationDate.setText(getNotificationDate(notification.dateInMilliseconds));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //TODO: open web browser.
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private static String getNotificationDate(long dateInMilliseconds) {
        return "Hace 2 minutos";
    }

}
