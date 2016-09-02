package com.example.android.booklisting.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.booklisting.Adapter.BookAdapter;
import com.example.android.booklisting.Class.Book;
import com.example.android.booklisting.Class.BookNetwork;
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
//    OkHttpClient client = new OkHttpClient();
//    ArrayList<Book> book = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        final Request request = new Request.Builder()
//                .url("https://www.googleapis.com/books/v1/volumes?q=android&maxResults=1")
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                System.out.println("You suck dude.");
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                book = new ArrayList<>(extractBookFromJson(response.body().string()));
//                //updateUI(book);
//            }
//        });

        BookNetwork bookNetwork = new BookNetwork();
        ArrayList<Book> book = bookNetwork.getBook();

        BookAdapter adapter = new BookAdapter(this, book);
        ListView bookListView = (ListView) findViewById(R.id.book_list);
        bookListView.setAdapter(adapter);
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

//    private void updateUI(final ArrayList<Book> book) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                for (int i = 0; i < book.size(); i++) {
//
//                    TextView authorTextView = (TextView) findViewById(R.id.author_test);
//                    String[] authors = book.get(i).getAuthor();
//                    StringBuilder authorList = new StringBuilder();
//                    for (String author : authors) {
//                        authorList.append(author);
//                        authorList.append("\n");
//                    }
//                    authorTextView.setText(authorList);
//
//                    TextView titleTextView = (TextView) findViewById(R.id.title_test);
//                    titleTextView.setText(book.get(i).getTitle());
//
//                    TextView subTitleTextView = (TextView) findViewById(R.id.subtitle_test);
//                    subTitleTextView.setText(book.get(i).getSubTitle());
//                }
//            }
//        });
//    }
}
