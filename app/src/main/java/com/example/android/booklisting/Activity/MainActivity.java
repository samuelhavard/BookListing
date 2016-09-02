package com.example.android.booklisting.Activity;


import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;

import com.example.android.booklisting.Adapter.BookAdapter;
import com.example.android.booklisting.Class.Book;
import com.example.android.booklisting.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BookAsyncTask task = new BookAsyncTask();
        task.execute();
    }

    private void updateUI (ArrayList<Book> books) {
        BookAdapter adapter = new BookAdapter(this, books);
        ListView bookListView = (ListView) findViewById(R.id.list);
        bookListView.setAdapter(adapter);
    }

    private class BookAsyncTask extends AsyncTask<URL, Void, ArrayList<Book>> {

        @Override
        protected ArrayList<Book> doInBackground(URL... urls) {
            URL url = createUrl("https://www.googleapis.com/books/v1/volumes?q=android");
            String jsonResponse = "";

            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return extractBookFromJson(jsonResponse);
        }

        @Override
        protected void onPostExecute(ArrayList<Book> book) {
            if (book == null) {
                return;
            }
            updateUI(book);
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

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            if (url == null) {
                return jsonResponse;
            }
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e("HTTP_TAG", "URL connection error " + urlConnection.getResponseCode());
                }
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
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


                    JSONObject imageInfo = volumeInfo.getJSONObject("imageLinks");
                    String thumbnailURL = imageInfo.getString("smallThumbnail");

                    books.add(new Book(authors, title, subTitle, thumbnailURL));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return books;
        }
    }
}


