package com.example.android.booklisting.Adapter;


import android.content.Context;
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
 * Created by samue_000 on 9/1/2016.
 */
public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_item, parent, false);
        }

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

        StringBuilder authorsString = new StringBuilder();
        for (int i = 0; i < currentBook.getAuthor().length; i++) {
            authorsString.append(currentBook.getAuthor()[i]);
            if (i < currentBook.getAuthor().length - 1) {
                authorsString.append("\n");
            }
        }
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author_text_view);
        authorTextView.setText(authorsString);

        URL thumbURL = createUrl(currentBook.getBookURL());

        ImageView thumbImage = (ImageView) listItemView.findViewById(R.id.book_image_view);
        Picasso.with(getContext())
                .load(String.valueOf(thumbURL))
                .into(thumbImage);

        return listItemView;
    }

    private URL createUrl(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return url;
    }
}
