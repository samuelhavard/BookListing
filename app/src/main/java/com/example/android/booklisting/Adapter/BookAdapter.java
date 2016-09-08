package com.example.android.booklisting.Adapter;


import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.booklisting.Class.Book;
import com.example.android.booklisting.R;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * {@link BookAdapter} is used to fill the ListView with {@link Book} objects
 */
public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    public static final String LOG_TAG = BookAdapter.class.getSimpleName();

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_item, parent, false);
        }

        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author_text_view);
        ImageView thumbImage = (ImageView) listItemView.findViewById(R.id.book_image_view);

        Book currentBook = getItem(position);

        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title_text_view);
        titleTextView.setText(currentBook.getTitle());

        TextView subTitleTextView = (TextView) listItemView.findViewById(R.id.sub_title_text_view);
        if (currentBook.getHasSubTitle()) {
            subTitleTextView.setVisibility(View.VISIBLE);
            subTitleTextView.setText(currentBook.getSubTitle());
        } else {
            subTitleTextView.setVisibility(View.GONE);
        }

        if (!currentBook.getInformationOnly()) {
            StringBuilder authorsString = new StringBuilder();
            for (int i = 0; i < currentBook.getAuthor().length; i++) {
                authorsString.append(currentBook.getAuthor()[i]);
                if (i < currentBook.getAuthor().length - 1) {
                    authorsString.append("\n");
                }
            }
            authorTextView.setVisibility(View.VISIBLE);
            authorTextView.setText(authorsString);
        }

        if (!currentBook.getInformationOnly()) {
            thumbImage.setVisibility(View.VISIBLE);
            URL thumbURL = createUrl(currentBook.getBookURL());

            Picasso.with(getContext())
                    .load(String.valueOf(thumbURL))
                    .into(thumbImage);
        }

        if (currentBook.getInformationOnly()) {
            authorTextView.setVisibility(View.GONE);
            thumbImage.setVisibility(View.GONE);
            titleTextView.setGravity(Gravity.CENTER);
            subTitleTextView.setGravity(Gravity.CENTER);
        }

        return listItemView;
    }

    /**
     * createURL is a helper method used to check the correctness of the URL for the image
     *
     * @param urlString is a URL as a {@link String} to be checked for correctness
     * @return {@link URL} that has been checked for correctness.
     */
    private URL createUrl(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating URL", e);
            return null;
        }
        return url;
    }
}
