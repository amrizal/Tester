package com.amrizal.example.barcodetester;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private static final String SEARCH_ISBN_GOOGLE_BOOKS = "com.amrizal.example.barcodetester.ResultActivity.SEARCH_ISBN_GOOGLE_BOOKS";
    private static final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String TAG = ResultActivity.class.getSimpleName();

    RecyclerView resultListView;
    ResultListAdapter adapter;
    List<Result> resultList = new ArrayList<>();
    String isbn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultListView = (RecyclerView)findViewById(R.id.result_list);

        adapter = new ResultListAdapter(this, resultList);
        resultListView.setAdapter(adapter);

        Intent intent =getIntent();
        if(intent != null){
            isbn = intent.getStringExtra(EXTRA.ISBN);
            onSearchIsbn(isbn);
        }
    }

    private void onSearchIsbn(String isbn) {
        if(isbn == null || isbn.isEmpty()){
            return;
        }

        String url = GOOGLE_BOOKS_URL + isbn;

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        onIsbnResult(response);
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                pDialog.hide();
            }
        });

        AppController.getInstance().addToRequestQueue(request, SEARCH_ISBN_GOOGLE_BOOKS);
    }

    private void onIsbnResult(JSONObject response) {
        resultList.clear();
        try {
            int totalItems = response.getInt("totalItems");
            JSONArray itemArray = response.getJSONArray("items");
            for(int i=0; i<totalItems; i++){
                JSONObject item = (JSONObject) itemArray.get(i);
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");

                Result result = new Result();
                String title = volumeInfo.getString("title");
                if(!volumeInfo.isNull("subtitle")){
                    title += "-" + volumeInfo.getString("subtitle");
                }
                result.setTitle(title);
                JSONArray authors = volumeInfo.getJSONArray("authors");
                String author = "";
                for(int j=0; j<authors.length(); j++){
                    if(author.length() > 0){
                        author += ", ";
                    }
                    author += authors.getString(j);
                }
                result.setAuthor(author);
                result.setDescription(volumeInfo.getString("description"));

                if(!volumeInfo.isNull("imageLinks")){
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    result.setThumbnailPath(imageLinks.getString("thumbnail"));
                }

                if(!volumeInfo.isNull("industryIdentifiers")){
                    JSONArray industryIdentifiers = volumeInfo.getJSONArray("industryIdentifiers");
                    for(int j=0; j<industryIdentifiers.length(); j++){
                        JSONObject industryIdentifier = industryIdentifiers.getJSONObject(j);
                        if(industryIdentifier.getString("type").compareToIgnoreCase("ISBN_13") == 0){
                            result.setIsbn(industryIdentifier.getString("identifier"));
                            break;
                        }
                    }
                }

                resultList.add(result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }
}
