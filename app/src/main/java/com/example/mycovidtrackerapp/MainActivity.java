package com.example.mycovidtrackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycovidtrackerapp.api.ApiInterface;
import com.example.mycovidtrackerapp.api.ApiUtility;
import com.example.mycovidtrackerapp.api.CountryData;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.io.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
{
    private TextView TotalConfirmed, TodayConfirmed, TotalActive, TotalRecovered, TodayRecovered, TotalDeath, TodayDeath, TotalTest;
    private TextView dateTV;
    private PieChart pieChart;

    private List<CountryData> list;

    String country = "India";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new ArrayList<>();

        if(getIntent().getStringExtra("country") != null)
            country = getIntent().getStringExtra("country");

        init();

        TextView cname = findViewById(R.id.cname);
        cname.setText(country);

        cname.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, CountryActivity.class)));

        ApiUtility.getApiInterface().getCountryData()
                .enqueue(new Callback<List<CountryData>>()
                {
                    @Override
                    public void onResponse(Call<List<CountryData>> call, Response<List<CountryData>> response)
                    {
                        list.addAll(response.body());

                        for(int i = 0; i<list.size(); i++)
                        {
                            if(list.get(i).getCountry().equals(country))
                            {
                                int confirmed = Integer.parseInt(list.get(i).getCases());
                                int active = Integer.parseInt(list.get(i).getActive());
                                int recovered = Integer.parseInt(list.get(i).getRecovered());
                                int death = Integer.parseInt(list.get(i).getDeaths());

                                TotalConfirmed.setText(NumberFormat.getInstance().format(confirmed));
                                TotalActive.setText(NumberFormat.getInstance().format(active));
                                TotalRecovered.setText(NumberFormat.getInstance().format(recovered));
                                TotalDeath.setText(NumberFormat.getInstance().format(death));

                                TodayDeath.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayDeaths())));
                                TodayConfirmed.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayCases())));
                                TodayRecovered.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayRecovered())));
                                TotalTest.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTests())));

                                setText(list.get(i).getUpdated());

                                pieChart.addPieSlice(new PieModel("Confirmed", confirmed, getResources().getColor(R.color.yellow)));
                                pieChart.addPieSlice(new PieModel("Active", active, getResources().getColor(R.color.blue_pie)));
                                pieChart.addPieSlice(new PieModel("Recovered", recovered, getResources().getColor(R.color.green_pie)));
                                pieChart.addPieSlice(new PieModel("Death", death, getResources().getColor(R.color.red_pie)));

                                pieChart.startAnimation();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CountryData>> call, Throwable t)
                    {
                        Toast.makeText(MainActivity.this, "Error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setText(String updated)
    {
        DateFormat format = new SimpleDateFormat("MMM dd, yyyy");
        long milliseconds = Long.parseLong(updated);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        dateTV.setText("Updated at "+format.format(calendar.getTime()));
    }

    private void init()
    {
        TotalConfirmed = findViewById(R.id.TotalConfirmed);
        TotalActive = findViewById(R.id.TotalActive);
        TotalRecovered = findViewById(R.id.TotalRecovered);
        TotalDeath = findViewById(R.id.TotalDeath);
        TotalTest = findViewById(R.id.TotalTest);
        TodayConfirmed = findViewById(R.id.TodayConfirmed);
        TodayRecovered = findViewById(R.id.TodayRecovered);
        TodayDeath = findViewById(R.id.TodayDeath);

        pieChart = findViewById(R.id.pieChart);
        dateTV = findViewById(R.id.date);
    }
}