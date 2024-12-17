package com.example.fitnesstracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Custom ArrayAdapter for displaying a list of feed items in a ListView.
 * Binds data from a list of {@link FeedItem} objects to views in a layout resource.
 */
public class FeedAdapter extends ArrayAdapter<FeedItem> {

    /**
     * Constructor for the FeedAdapter.
     *
     * @param context   The current context, used to inflate the layout file.
     * @param feedItems The list of {@link FeedItem} objects to display in the ListView.
     */
    public FeedAdapter(Context context, ArrayList<FeedItem> feedItems) {
        super(context, 0, feedItems);
    }

    /**
     * Provides a view for an adapter view (ListView) to display a feed item.
     *
     * @param position    The position of the data in the adapter's data set.
     * @param convertView The recycled view to populate, or null if a new view is required.
     * @param parent      The parent view that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the FeedItem object for the current position
        FeedItem feedItem = getItem(position);

        // Inflate a new view if no reusable view is available
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.feed_item, parent, false);
        }

        // Find the TextView in the feed_item layout
        TextView messageTextView = convertView.findViewById(R.id.messageTextView);

        // Set the message text if the FeedItem is not null
        if (feedItem != null) {
            messageTextView.setText(feedItem.getMessage());
        }

        // Return the populated view
        return convertView;
    }
}
