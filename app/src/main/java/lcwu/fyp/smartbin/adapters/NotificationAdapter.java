package lcwu.fyp.smartbin.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import lcwu.fyp.smartbin.R;
import lcwu.fyp.smartbin.activities.NotificationActivity;
import lcwu.fyp.smartbin.model.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    private List<Notification> data;
    Context context;
    private int roll;
    private NotificationActivity notificationActivity;

    public NotificationAdapter(Context context , int r  , NotificationActivity notificationActivity) {
        data = new ArrayList<>();
        this.context = context;
        roll =r;
        this.notificationActivity = notificationActivity;
    }

    public void setData(List<Notification> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification,parent,false);
        return new NotificationHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        final Notification notification = data.get(position);
        if(notification != null){
        holder.date.setText(notification.getDate());
        if(roll == 0 ){
                holder.message.setText(notification.getUserText());
        }else {
            holder.message.setText(notification.getDriverText());
        }
        holder.mainCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationActivity.showBottomSheet(notification);
            }
        });

      }else {
            Log.e("Notification", "Notification obj is null");

        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class NotificationHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView message;
        CardView mainCard;

        public NotificationHolder(@NonNull View itemView) {

            super(itemView);
            date = itemView.findViewById(R.id.Date);
            message = itemView.findViewById(R.id.message);
            mainCard = itemView.findViewById(R.id.mainCard);
        }
    }
}
