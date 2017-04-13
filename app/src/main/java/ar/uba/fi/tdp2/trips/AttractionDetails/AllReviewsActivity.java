package ar.uba.fi.tdp2.trips.AttractionDetails;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import ar.uba.fi.tdp2.trips.BackendService;
import ar.uba.fi.tdp2.trips.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllReviewsActivity extends AppCompatActivity {
    String attractionName;
    int attractionId;
    List<Review> reviews;
    RV_ReviewsAdapter adapter;
    LinearLayoutManager llm;
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_reviews_activity);

        final String LOGTAG = getString(R.string.app_name);

        Bundle bundle = getIntent().getExtras();
        attractionName = bundle.getString("attractionName");
        attractionId = bundle.getInt("attractionId");
        System.out.println("attractionName: " + attractionName);
        System.out.println("attractionId: " + String.valueOf(attractionId));


        llm = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.reviews_recycler_view);
        recyclerView.setLayoutManager(llm);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setTitle(attractionName);
        System.out.println("SET TITLE: " + attractionName);

        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        Call<List<Review>> call = backendService.getReviews(attractionId);
        System.out.println("attractionId: " + String.valueOf(attractionId));

        call.enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                Log.d(LOGTAG, "statusCode: " + response.code());
                Log.d(LOGTAG, getString(R.string.got_reviews) + response.body().toString());
                reviews = response.body();

                adapter = new RV_ReviewsAdapter(reviews);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(),getString(R.string.no_server_error), Toast.LENGTH_LONG).show();
                Log.d(LOGTAG, t.toString());
            }
        });
    }
}
