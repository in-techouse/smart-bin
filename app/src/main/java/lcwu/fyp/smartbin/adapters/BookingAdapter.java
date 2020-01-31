package lcwu.fyp.smartbin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lcwu.fyp.smartbin.R;
import lcwu.fyp.smartbin.model.Booking;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingHolder> {
    private List<Booking> data;

    public BookingAdapter() {
        data = new ArrayList<>();
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
        final Booking booking = data.get(position);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class BookingHolder extends RecyclerView.ViewHolder{

        public BookingHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
