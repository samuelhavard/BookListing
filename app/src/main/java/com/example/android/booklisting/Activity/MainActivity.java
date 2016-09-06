package com.example.android.booklisting.Activity;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

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

    private class BookAsyncTask extends AsyncTask<URL, Void, ArrayList<Book>> {

        private final String API_KEY = "AIzaSyB6e_6sra6ky-TmZ0-5lbsjXkbJw9tNm3A";

        private void updateUI(ArrayList<Book> books) {
            BookAdapter adapter;
            ListView bookListView = (ListView) findViewById(R.id.list);
                adapter = new BookAdapter(getBaseContext(), books);
                bookListView.setAdapter(adapter);
        }

        @Override
        protected ArrayList<Book> doInBackground(URL... urls) {
            //URL url = createUrl("https://www.googleapis.com/books/v1/volumes?q=Stephen+King&key="+API_KEY);
            URL url = createUrl("https://www.googleapis.com/books/v1/volumes?q=uytuytutu&key=" + API_KEY);
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

        /**
         * @param urlString
         * @return
         */
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

        /**
         * @param url
         * @return
         * @throws IOException
         */
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
            } catch (IOException e) {
                e.printStackTrace();
                return null;
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

        /**
         * @param inputStream
         * @return
         * @throws IOException
         */
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

        /**
         * extractBookFromJson is a helper mehtod used to parse the JSON data retrieved from
         * the API into an {@link ArrayList<Book>}.
         *
         * @param bookJSON the string retrieved from the API
         * @return {@link ArrayList<Book>} with information parsed from the JSON response from
         * the API
         */
        private ArrayList<Book> extractBookFromJson(String bookJSON) {
            ArrayList<Book> books = new ArrayList<>();

            if (TextUtils.isEmpty(bookJSON)) {
                return null;
            }

            try {
                //Root JSON object and the JSON array of books in the
                JSONObject baseBookResponse = new JSONObject(bookJSON);
                if (baseBookResponse.has("items")) {
                    JSONArray bookArray = baseBookResponse.getJSONArray("items");

                    //
                    for (int i = 0; i < bookArray.length(); i++) {
                        JSONObject arrayObject = bookArray.getJSONObject(i);
                        JSONObject volumeInfo = arrayObject.getJSONObject("volumeInfo");
                        JSONArray authorsArray = volumeInfo.getJSONArray("authors");

                        //Parse the authors into an array of String
                        String[] authors = new String[authorsArray.length()];
                        for (int j = 0; j < authorsArray.length(); j++) {
                            authors[j] = authorsArray.getString(j);
                        }

                        //Parse the title and append it to a string builder.  If there is a subTitle
                        //parse it and append it below the title
                        String title = volumeInfo.getString("title");
                        String subTitle = "";
                        if (volumeInfo.has("subtitle")) {
                            subTitle = volumeInfo.getString("subtitle");
                        }

                        //Parse the thumbnail URL picture as a String
                        JSONObject imageInfo = volumeInfo.getJSONObject("imageLinks");
                        String thumbnailURL = imageInfo.getString("smallThumbnail");

                        //adds the parsed data into a new Book object and added to the
                        //ArrayList
                        if (volumeInfo.has("subtitle")) {
                            books.add(new Book(authors, title, subTitle, thumbnailURL));
                        } else {
                            books.add(new Book(authors, title, thumbnailURL));
                        }
                    }
                } else {
                    String noBookTitle = "We were unable to find a book related to your search";
                    String noBookSubTitle = "Please try another search";
                    String[] noBookAuthor = {""};
                    String noBookThumbnail = "";

                    books.add(new Book(noBookAuthor, noBookTitle, noBookSubTitle, noBookThumbnail));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return books;
        }
    }
}


