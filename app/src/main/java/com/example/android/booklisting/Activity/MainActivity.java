package com.example.android.booklisting.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.example.android.booklisting.Class.Book;
import com.example.android.booklisting.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Request request = new Request.Builder()
                .url("https://www.googleapis.com/books/v1/volumes?q=android&maxResults=1")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("You suck dude.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ArrayList<Book> book = new ArrayList<>(extractBookFromJson(response.body().string()));
                updateUI(book);
            }
        });
    }

    private ArrayList<Book> extractBookFromJson(String bookJSON) {
        ArrayList<Book> books = new ArrayList<>();

        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }
        try {
            JSONObject baseBookResponse = new JSONObject(bookJSON);
            JSONArray bookArray = baseBookResponse.getJSONArray("items");

            for (int i = 0; i < bookArray.length(); i++) {
                JSONObject arrayObject = bookArray.getJSONObject(i);
                JSONObject volumeInfo = arrayObject.getJSONObject("volumeInfo");
                JSONArray authorsArray = volumeInfo.getJSONArray("authors");

                String[] authors = new String[authorsArray.length()];
                for (int j = 0; j < authorsArray.length(); j++) {
                    authors[j] = authorsArray.getString(j);
                }

                String title = volumeInfo.getString("title");
                String subTitle = volumeInfo.getString("subtitle");

                books.add(new Book(authors, title, subTitle));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return books;
    }

    private void updateUI (final ArrayList<Book> book) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView text1 = (TextView) findViewById(R.id.author_test);
                String[] authors = book.get(0).getAuthor();
                text1.setText(authors[0]);

                TextView text2 = (TextView) findViewById(R.id.title_test);
                text2.setText(book.get(0).getTitle());

                TextView text3 = (TextView) findViewById(R.id.subtitle_test);
                text3.setText(book.get(0).getSubTitle());
            }
        });
    }
}
