package lcwu.fyp.smartbin.adapters;

import android.content.Context;
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
import lcwu.fyp.smartbin.activities.BookingActivity;
import lcwu.fyp.smartbin.model.Booking;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingHolder> {

    private List<Booking> data;
    private Context context;
    private BookingActivity bookingActivity;


    public BookingAdapter(Context c , BookingActivity b)
    {
        data = new ArrayList<>();
        context = c;
        bookingActivity = b;

    }

    public void setData(List<Booking> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking,parent,false);
        return new BookingHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingHolder holder, int position) {

        final Booking b = data.get(position);
        holder.date.setText(b.getStartTime());
//        holder.trashWeight.setText(b.getTrashWeight()+"");
        holder.status.setText(b.getStatus());
        holder.address.setText(b.getPickup());

        holder.mainCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookingActivity.showBottomSheet(b);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class BookingHolder extends RecyclerView.ViewHolder{
        TextView date, trashWeight, status, address;
        CardView mainCard;

        public BookingHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            trashWeight = itemView.findViewById(R.id.trashWeight);
            mainCard = itemView.findViewById(R.id.mainCard);
            status = itemView.findViewById(R.id.status);
            address = itemView.findViewById(R.id.address);
        }
    }
}
