package uk.ac.wlv.assessment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    Activity activity;
    private Context context;
    private ArrayList<String> message_id, message_title, message, image_path;

    CustomAdapter(Activity activity, Context context, ArrayList<String> message_id, ArrayList<String> message_title, ArrayList<String> message, ArrayList<String> image_path) {
        this.activity = activity;
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
        holder.message_id_txt.setText(message_id.get(position));
        holder.message_title_txt.setText(message_title.get(position));
        holder.message_txt.setText(message.get(position));

        String imagePath = image_path.get(position);
        if (imagePath != null && !imagePath.isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap != null) {
                holder.message_thumbnail.setImageBitmap(bitmap);
            } else {
                holder.message_thumbnail.setImageResource(R.drawable.image_placeholder);
            }
        } else {
            holder.message_thumbnail.setImageResource(R.drawable.image_placeholder);
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, ViewMsg.class);
                    intent.putExtra("id", message_id.get(currentPosition));
                    intent.putExtra("title", message_title.get(currentPosition));
                    intent.putExtra("message", message.get(currentPosition));
                    intent.putExtra("imagePath", image_path.get(currentPosition));
                    activity.startActivityForResult(intent, 1);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return message_id.size();
    }

    // Update data method to refresh the RecyclerView based on the search results
    public void updateData(ArrayList<String> filteredMessageId, ArrayList<String> filteredMessageTitle, ArrayList<String> filteredMessage, ArrayList<String> filteredImagePath) {
        this.message_id = filteredMessageId;
        this.message_title = filteredMessageTitle;
        this.message = filteredMessage;
        this.image_path = filteredImagePath;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView message_id_txt, message_title_txt, message_txt;
        ImageView message_thumbnail;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            message_id_txt = itemView.findViewById(R.id.message_id_text);
            message_title_txt = itemView.findViewById(R.id.message_title_text);
            message_txt = itemView.findViewById(R.id.message_text);
            message_thumbnail = itemView.findViewById(R.id.message_thumbnail);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
