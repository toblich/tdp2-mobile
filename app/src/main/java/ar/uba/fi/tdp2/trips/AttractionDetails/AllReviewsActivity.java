package ar.uba.fi.tdp2.trips.AttractionDetails;

import android.content.Context;
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
import ar.uba.fi.tdp2.trips.Utils;
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
    private Context localContext = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_reviews_activity);

        Bundle bundle = getIntent().getExtras();
        attractionName = bundle.getString("attractionName");
        attractionId = bundle.getInt("attractionId");

        llm = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.reviews_recycler_view);
        recyclerView.setLayoutManager(llm);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setTitle(attractionName);
        System.out.println("SET TITLE: " + attractionName);

        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        Call<List<Review>> call = backendService.getReviews(attractionId);

        call.enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.body() == null) {
                    return;
                }
                reviews = response.body();

                adapter = new RV_ReviewsAdapter(reviews);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(localContext,getString(R.string.no_server_error), Toast.LENGTH_LONG).show();
                Log.d(Utils.LOGTAG, t.toString());
            }
        });
    }
}
