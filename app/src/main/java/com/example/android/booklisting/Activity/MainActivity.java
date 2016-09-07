package com.example.android.booklisting.Activity;


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

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String ERROR = "error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BookAsyncTask task = new BookAsyncTask();
        task.execute();
    }

    /**
     *  BookAsyncTask is used to retrieve and parse API data over the internet.
     *
     *  AsyncTask enables proper and easy use of the UI thread. This class allows you to
     *  perform background operations and publish results on the UI thread without having to
     *  manipulate threads and/or handlers.
     */
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
            URL url = createUrl("https://www.googleapis.com/books/v1/volumes?q=Stephen+King&key=" + API_KEY);
            //URL url = createUrl("https://www.googleapis.com/books/v1/volumes?q=uytuytutu&key=" + API_KEY);
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
         * createUrl is a helper method that is used to check the URL input for correctness
         *
         * @param urlString is a {@link String} version of a URL to be parsed for correctness
         * @return a properly formed {@link URL} or {@link null}
         */
        private URL createUrl(String urlString) {
            URL url;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, getString(R.string.url_error), e);
                return null;
            }
            return url;
        }

        /**
         * makeHttpRequest is a helper method that creates the URL connection and sends
         * the input stream to the helper method readFromStream to be converted into a
         * java String object.
         *
         * @param url is a {@link URL} to be used in the {@link HttpURLConnection}
         * @return a JSON response as a {@link String}
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
                    Log.e(LOG_TAG, "URL connection error " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                e.printStackTrace();
                jsonResponse = ERROR;
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
         * readFromStream is a helper method to help convert the input stream into a java String
         * object that can then be parsed.
         *
         * @param inputStream is an {@link InputStream} read from the {@link HttpURLConnection}
         * @return the {@link InputStream} as a {@link String}
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
                if (!(bookJSON.equals(ERROR))) {
                    //Root JSON object and the JSON array of books in the
                    JSONObject baseBookResponse = new JSONObject(bookJSON);
                    if (baseBookResponse.has("items")) {
                        JSONArray bookArray = baseBookResponse.getJSONArray("items");

                        //loop over the JSONArray to retrieve each JSON string
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
                        //if the search returned without any responses let the user know
                        String noBookTitle = getString(R.string.no_related_search);
                        String noBookSubTitle = getString(R.string.please_try_again);
                        books.add(new Book(noBookTitle, noBookSubTitle));
                    }
                } else if (bookJSON.equals(ERROR)) {
                    //if there is an issue contacting the server let the user know
                    String noBookTitle = getString(R.string.unable_to_contact_server);
                    String noBookSubTitle = getString(R.string.please_try_again);
                    books.add(new Book(noBookTitle, noBookSubTitle));
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing data from JSON results", e);
                return null;
            }
            return books;
        }
    }
}


