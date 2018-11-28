package com.bbagliotto.reto10.reto10;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    private ArrayList<Club> clubes;
    private ArrayList<Club> filteredClubes;

    private String url;
    private String urlSearch;
    private boolean modeSearch;
    private JSONArray json;
    private boolean dataReady;


    private RecyclerView mList;
    private MyAdapter mAdapter;
    private Button mButton;
    private EditText mSearchBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: init main activity");

        mList = (RecyclerView) findViewById(R.id.list);
        mButton = (Button) findViewById(R.id.search_but);
        mSearchBar = (EditText) findViewById(R.id.filter);

        clubes = new ArrayList<>();
        filteredClubes = new ArrayList<>();
        dataReady = false;

        modeSearch = getIntent().getBooleanExtra("modeSearch",false);
        /*if(getIntent().hasExtra("urlSearch")) {
            urlSearch = getIntent().getStringExtra("urlSearch");
        }
        else {
            urlSearch = "";
        }*/

        Log.d(TAG, "onCreate: modeSearch="+modeSearch+" urlSearch="+urlSearch);

        new DownloadFilesTask().execute();
        while(!dataReady){}

        //Log.d(TAG, clubes.toString());

        actualizeView(clubes);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchBarContent = mSearchBar.getText().toString();
                if (searchBarContent.equals("")){
                    filteredClubes = clubes;
                    actualizeView(clubes);
                }
                else{
                    filteredClubes = new ArrayList<>();
                    for (Club c : clubes) {
                        if (c.getDireccion().toLowerCase().contains(searchBarContent.toLowerCase())
                                || c.getDisciplina().toLowerCase().contains(searchBarContent)
                                || c.getNombre().toLowerCase().contains(searchBarContent)
                                || c.getTelefono().toLowerCase().contains(searchBarContent)
                                || c.getRepresentante().toLowerCase().contains(searchBarContent)) {
                            filteredClubes.add(c);
                        }
                    }
                    actualizeView(filteredClubes);
                }
            }
        });
    }


    private void actualizeView (ArrayList clubes){
        mAdapter = new MyAdapter(clubes);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mList.setLayoutManager(mLayoutManager);
        mList.setItemAnimator(new DefaultItemAnimator());
        mList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mList.setAdapter(mAdapter);
    }


    public static JSONArray requestWebService(String serviceUrl) {
        disableConnectionReuseIfNecessary();

        HttpURLConnection urlConnection = null;

        Log.d("MainActivity", "start of requestWebServices");

        try {

            Log.d(TAG, "requestWebService: init ");

            // create connection
            URL urlToRequest = new URL(serviceUrl);
            urlConnection = (HttpURLConnection)urlToRequest.openConnection();

            // handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                // handle unauthorized (if service requires user login)
                Log.d(TAG, "requestWebService: http unauthorized");

            } else if (statusCode != HttpURLConnection.HTTP_OK) {
                // handle any other errors, like 404, 500,..
                Log.d(TAG, "requestWebService: error "+statusCode);

            }

            // create JSON object from content
            InputStream in = new BufferedInputStream(
                    urlConnection.getInputStream());
            return new JSONArray(getResponseText(in));

        } catch (MalformedURLException e) {
            // URL is invalid
        } catch (SocketTimeoutException e) {
            // data retrieval or connection timed out
        } catch (IOException e) {
            // could not read response body
            // (could not create input stream)
        } catch (JSONException e) {
            // response body is no valid JSON string
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }


    /**
     * required in order to prevent issues in earlier Android version.
     */
    private static void disableConnectionReuseIfNecessary() {
        // see HttpURLConnection API doc
        if (Integer.parseInt(Build.VERSION.SDK)
                < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    private static String getResponseText(InputStream inStream) {
        // very nice trick from
        // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
        return new Scanner(inStream).useDelimiter("\\A").next();
    }

    public void chargeData(JSONArray json) {
        Log.d(TAG, "prepareData: init");

        if (json != null) {
            int len = json.length();
            Log.d(TAG, "prepareData: tab not null and length : "+len);

            for (int i = 0; i < len; i++) {
                JSONObject object;
                try {
                    object = json.getJSONObject(i);
                    Club club = new Club();
                    club.setNombre(object.getString("nombres_del_club_deportivo"));
                    club.setDisciplina(object.getString("disciplina_deportiva"));
                    club.setRepresentante(object.getString("representante_legal"));
                    club.setDireccion(object.getString("direccion"));
                    club.setTelefono(object.getString("telefono"));
                    //Log.d(TAG, club.toString());
                    clubes.add(club);
                    filteredClubes.add(club);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {

            long totalSize = 0;
            if (!modeSearch)
                url = "https://www.datos.gov.co/resource/98ca-rfer.json";
            else {
                url = urlSearch;
            }
            json = requestWebService(url);
            chargeData(json);
            dataReady = true;
            return totalSize;
        }
    }
}
