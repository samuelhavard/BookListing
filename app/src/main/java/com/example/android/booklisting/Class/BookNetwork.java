package com.example.android.booklisting.Class;

import android.text.TextUtils;

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

/**
 * Created by samue_000 on 9/1/2016.
 */
public class BookNetwork {
    private OkHttpClient client = new OkHttpClient();
    private ArrayList<Book> book = new ArrayList<>();
    private String bookJSONString;


    public ArrayList<Book> getBook() {
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/books/v1/volumes?q=android&maxResults=1")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("You suck dude.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //book = extractBookFromJson(response.body().string());
                bookJSONString = response.body().string();
            }
        });
        book = extractBookFromJson(bookJSONString);
        return book;
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
}
