package uk.ac.wlv.assessment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;

// Class for managing the RecyclerView displaying messages
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    Activity activity; // References the activity for use in the adapter
    private Context context; // References the context for use in the adapter
    private ArrayList<String> message_id, message_title, message, image_path;
    private HashSet<String> selectedMessages; // Tracks selected message IDs
    private OnSelectionChangeListener selectionChangeListener;

    CustomAdapter(Activity activity, Context context, ArrayList<String> message_id, ArrayList<String> message_title, ArrayList<String> message, ArrayList<String> image_path) {

        this.activity = activity;
        this.context = context;
        this.message_id = message_id;
        this.message_title = message_title;
        this.message = message;
        this.image_path = image_path;
        this.selectedMessages = new HashSet<>(); // Initialise set of selected messages
    }

    // Interface to notify if the selection state changes
    public interface OnSelectionChangeListener {
        void onSelectionChange(boolean hasSelection);
    }

    // Setter for the selection change listener
    public void setOnSelectionChangeListener(OnSelectionChangeListener listener) {
        this.selectionChangeListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a view representing each item in Recycler view
        LayoutInflater inflater = LayoutInflater.from(context);
        // Create references to the views
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Set message details in the corresponding views
        holder.message_id_txt.setText(message_id.get(position));
        holder.message_title_txt.setText(message_title.get(position));
        holder.message_txt.setText(message.get(position));

        // Load and set the image thumbnail
        String imagePath = image_path.get(position);
        if (imagePath != null && !imagePath.isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath); // Decode image to bitmap
            if (bitmap != null) {
                holder.message_thumbnail.setImageBitmap(bitmap); // If successful, display in view
            } else {
                holder.message_thumbnail.setImageResource(R.drawable.image_placeholder); // If unsuccessful, diplay placeholder
            }
        } else {
            holder.message_thumbnail.setImageResource(R.drawable.image_placeholder); // If image is missing, display placeholder
        }

        // Update checkbox based on whether the message is selected
        holder.checkbox_select.setChecked(selectedMessages.contains(message_id.get(position)));

        // To handle checkbox click events
        holder.checkbox_select.setOnClickListener(v -> {
            String currentId = message_id.get(position);
            if (holder.checkbox_select.isChecked()) {
                selectedMessages.add(currentId); // If selected, add to selectedMessages set
            } else {
                selectedMessages.remove(currentId); // If not selected, remove from selectedMessages set
            }
            // Notify the listener about selection changes
            if (selectionChangeListener != null) {
                selectionChangeListener.onSelectionChange(!selectedMessages.isEmpty());
            }
        });

        // Listener for handling row clicks - opens ViewMessage activity to show more detail
        holder.mainLayout.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition(); // Get the clicked position
            // If there is a position
            if (currentPosition != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context, ViewMsg.class); // create intent for ViewMsg activity
                // Send message details to the activity
                intent.putExtra("id", message_id.get(currentPosition));
                intent.putExtra("title", message_title.get(currentPosition));
                intent.putExtra("message", message.get(currentPosition));
                intent.putExtra("imagePath", image_path.get(currentPosition));
                // Start the activity
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    public HashSet<String> getSelectedMessages() {
        // Retrieve set of selected messages
        return selectedMessages;
    }

    @Override
    public int getItemCount() {
        // Return the total number of messages
        return message_id.size();
    }

    // Inner class - represents a ViewHolder for the RecyclerView items
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // User Interface components for a single row
        TextView message_id_txt, message_title_txt, message_txt;
        ImageView message_thumbnail;
        CheckBox checkbox_select;
        LinearLayout mainLayout;

        // Constructor, initialising the UI components
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            message_id_txt = itemView.findViewById(R.id.message_id_text);
            message_title_txt = itemView.findViewById(R.id.message_title_text);
            message_txt = itemView.findViewById(R.id.message_text);
            message_thumbnail = itemView.findViewById(R.id.message_thumbnail);
            checkbox_select = itemView.findViewById(R.id.checkbox_select);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }

    // Method to update the adapter's data
    public void updateData(ArrayList<String> newMessageId, ArrayList<String> newMessageTitle, ArrayList<String> newMessage, ArrayList<String> newImagePath) {
        this.message_id = newMessageId;
        this.message_title = newMessageTitle;
        this.message = newMessage;
        this.image_path = newImagePath;
        notifyDataSetChanged(); // Notify the adapter about the data change
    }
}


