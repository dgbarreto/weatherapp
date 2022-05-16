package com.avanade.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.avanade.weatherapp.adapaters.WeatherAdapter
import com.avanade.weatherapp.api.IWeatherEndpoints
import com.avanade.weatherapp.util.Constants
import com.avanade.weatherapp.util.NetWorkUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var rvWeather: RecyclerView
    lateinit var svCitySearch: SearchView
    lateinit var lvCities: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvWeather = findViewById(R.id.rvWeather)
        svCitySearch = findViewById(R.id.svCitySearch)
        lvCities = findViewById(R.id.lvCities)

        lvCities.setOnItemClickListener { parent, _, position, _ ->
            val queryText = parent.getItemAtPosition(position).toString()
            svCitySearch.setQuery(queryText, true)
        }

        setupSearchView()
    }

    private fun setupSearchView(){
        var cities: List<String> = arrayListOf()
        var cityAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, cities)

        Observable.create(ObservableOnSubscribe<Pair<String,Boolean>>{ subscriber ->
            svCitySearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(newText: String?): Boolean {
                    subscriber.onNext(Pair<String,Boolean>(newText!!, true))
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    subscriber.onNext(Pair<String,Boolean>(newText!!, false))
                    return false
                }
            })
        })
            .map { Pair<String,Boolean>(it.first.lowercase(), it.second) }
            .debounce(250, TimeUnit.MILLISECONDS)
            .distinct()
            .filter{
                it.first.isNotBlank()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(localClassName, "Typed $it")
                var queryValue = it.first
                cities = resources.getStringArray(R.array.saCities).filter { it.lowercase().startsWith(queryValue) }.toList()
                cityAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, cities)
                lvCities.adapter = cityAdapter

                if(it.second){
                    searchForecast(it.first)
                }
            })
    }

    private fun resetSearch(){
        svCitySearch.clearFocus()
        val cities: List<String> = arrayListOf()
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, cities)
        lvCities.adapter = cityAdapter
    }

    private fun searchForecast(city: String){
        val network = NetWorkUtils.getRetrofitInstance(Constants.WEATHER_API_BASE_URL)
        val endpoint = network.create(IWeatherEndpoints::class.java)

        endpoint.requestWeatherForecast(city, 3)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                    val weatherData = it.forecast.ForecastDay
                    rvWeather.adapter = WeatherAdapter(weatherData)
                    resetSearch()
                },
                {
                    Log.d(localClassName, "Error ${it.message}")
                })
    }
}