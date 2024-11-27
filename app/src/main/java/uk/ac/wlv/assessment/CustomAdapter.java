package uk.ac.wlv.assessment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList message_id, message_title, message, image_path;

    // Constructor
    CustomAdapter(Context context, ArrayList message_id, ArrayList message_title, ArrayList message, ArrayList image_path ){
        this.context = context;
        this.message_id = message_id;
        this.message_title = message_title;
        this.message = message;
        this.image_path = image_path;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.message_id_txt.setText(String.valueOf(message_id.get(position)));
        holder.message_title_txt.setText(String.valueOf(message_title.get(position)));
        holder.message_txt.setText(String.valueOf(message.get(position)));

        // Add implementation of the adding image

    }

    @Override
    public int getItemCount() {
        return message_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView message_id_txt, message_title_txt, message_txt;
        ImageView message_thumbnail;
        // Constructor
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            message_id_txt = itemView.findViewById(R.id.message_id_text);
            message_title_txt = itemView.findViewById(R.id.message_title_text);
            message_txt = itemView.findViewById(R.id.message_text);
            message_thumbnail = itemView.findViewById(R.id.message_thumbnail);


        }
    }
}
