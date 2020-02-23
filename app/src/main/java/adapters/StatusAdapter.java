package adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import io.eyec.bombo.stokies.R;

public class StatusAdapter extends RecyclerView.Adapter<statusViewHolder> {
    Activity activity;
    private String[] document;
    private String[] date;

    public String[] getDocument() {
        return document;
    }

    public void setDocument(String[] document) {
        this.document = document;
    }

    public String[] getDate() {
        return date;
    }

    public void setDate(String[] date) {
        this.date = date;
    }

    public StatusAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public statusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statuslist, parent, false);
        return new statusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final statusViewHolder holder, int position) {
        holder.Document.setText(document[position]);
        holder.Date.setText(date[position]);
    }

    @Override
    public int getItemCount() {
        return document.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
        //return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        return position;
    }
}

class statusViewHolder extends RecyclerView.ViewHolder {
    public TextView Document;
    public TextView Date;

    public statusViewHolder(View view) {
        super(view);
        Document = view.findViewById(R.id.document);
        Date = view.findViewById(R.id.date);

    }
}

