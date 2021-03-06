package ru.ifmo.md.lesson5;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends Activity {
    private ArrayList<PostData> posts = new ArrayList<PostData>();
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.post_list);
        postAdapter = new PostAdapter(this, R.layout.post_item, posts);
        listView.setAdapter(postAdapter);
        listView.setOnItemClickListener(contentShower);

        RssDataController async = new RssDataController();
        async.execute(getString(R.string.feed_url));
    }

    private AdapterView.OnItemClickListener contentShower = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PostData data = posts.get(position);
            Intent postViewIntent = new Intent(MainActivity.this, PostViewActivity.class);
//            postViewIntent.putExtra("description", data.getPostDescription());
            postViewIntent.putExtra("url", data.getPostLink());
            startActivity(postViewIntent);
        }
    };

    private class RssDataController extends AsyncTask<String, Integer, ArrayList<PostData>> {
        @Override
        protected ArrayList<PostData> doInBackground(String... urls) {
            ArrayList<PostData> postDataList = null;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                PostParser postParser = new PostParser(connection.getInputStream());
                postDataList = postParser.parse();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return postDataList;
        }

        @Override
        protected void onPostExecute(ArrayList<PostData> result) {
            for (PostData aResult : result) {
                posts.add(aResult);
            }

            postAdapter.notifyDataSetChanged();
        }
    }
}
