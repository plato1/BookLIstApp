package com.abnanodegree.jk.booklistapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

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

/** LINKS:
 *   http://jsonformatter.org/
 *   https://developers.google.com/books/docs/v1/getting_started#intro
 *   https://www.googleapis.com/books/v1/volumes?q=quilting
 */

/** TODO:
 *  add searchable - https://androidhub.intel.com/en/posts/nglauber/Android_Search.html
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /** Tag for the log messages */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final int MAX_BOOKS_ON_LISTVIEW = 10;

    private static final String GBOOKS_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";


    String queryString;
    // create empty arraylist of Book objects into which queried book titles will be placed
    ArrayList<Book> bookList = new ArrayList<Book>();

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv=(ListView) findViewById(R.id.listView);
        BookAdapter bookAdapter = new BookAdapter(this, bookList);
        // Assign adapter to ListView
        lv.setAdapter(bookAdapter);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("onQueryTextSubmit", " this worked");

                if (query.length() >0) {
                    // clear current arraylist of all elements in prep for new search string
                    // garbage collector will automatically reclaim freed memory
                    bookList.clear();
                    // Build query string
                    queryString = GBOOKS_REQUEST_URL + query;
                    // Kick off an {@link AsyncTask} to perform the network request
                    GBooksAsyncTask task = new GBooksAsyncTask();
                    task.execute();
                    return true;
                }
                else
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("onQueryTextChange", " newText= "+newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Snackbar.make(getCurrentFocus(), "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
           // Toast.makeText(getApplicationContext(),"Replace with your own action", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class GBooksAsyncTask extends AsyncTask<URL, Void, Book> {

        @Override
        protected Book doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl(queryString);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            // Extract relevant fields from the JSON response and create an {@link Book} object
            Book book = extractFeatureFromJson(jsonResponse);

            // Return the {@link Book} object as the result of the {@link GBookAsyncTask}
            return book;
        }

        /**
         * Update the screen with the given book (which was the result of the
         * {@link GBooksAsyncTask}).
         */
        @Override
        protected void onPostExecute(Book book) {
            if (book == null) {
                return;
            }

         //   updateUi(book);
        }

        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            if (url == null) return jsonResponse;

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                int respCode;
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                respCode = urlConnection.getResponseCode();
                if (respCode == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                }
                else {
                    Log.e(LOG_TAG, "Error response code from urlConnection is"+respCode);
                }
            } catch (IOException e) {
                // TODO: Handle the exception
                Log.e(LOG_TAG,"IOException", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
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
         * Return an {@link Book} object by parsing out information
         * about the first book from the input bookJSON string.
         */
        private Book extractFeatureFromJson(String bookJSON) {
            if (TextUtils.isEmpty(bookJSON)) return null;
            try {
                JSONObject baseJsonResponse = new JSONObject(bookJSON);
                JSONArray featureArray = baseJsonResponse.getJSONArray("items");

                // If there are results in the features array...select max of ? volumes to display
                int totalBooks = featureArray.length();
                if (totalBooks > 0) {
                    String title="";
                    String author="";
                    String publishedDate="";
                    String price="";

                    // show smaller of max books that view can display or the total books returned from query
                    int booksToDisplay=0;
                    if (totalBooks <MAX_BOOKS_ON_LISTVIEW)
                        booksToDisplay=totalBooks;
                    else
                        booksToDisplay = MAX_BOOKS_ON_LISTVIEW;

                    // loop thru json and build list of books
                    for (int i=0; i<MAX_BOOKS_ON_LISTVIEW; i++){
                        // Extract out the first feature (which is an book)
                        JSONObject firstFeature = featureArray.getJSONObject(i);
                        JSONObject volumeInfo = firstFeature.getJSONObject("volumeInfo");

                        // Extract out the title, author, published date, and price of book
                        title = volumeInfo.getString("title");
                        // add book to arraylist
                        bookList.add(new Book(title,author,publishedDate,price));
                    }

                    // return 1st element in arraylist
                    return bookList.get(0);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
            }
            return null;
        }
    }

}
