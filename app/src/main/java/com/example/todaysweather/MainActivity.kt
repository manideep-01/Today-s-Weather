package com.example.todaysweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telecom.Call
import androidx.appcompat.widget.SearchView
import com.example.todaysweather.databinding.ActivityMainBinding
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetchWeatherdata("Kolkata")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherdata(query)
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }


    private fun fetchWeatherdata(cityName : String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(API_Interface::class.java)
        val response = retrofit.getWeatherData(cityName,"975d59107381843fb8609f5883116c9d", "metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(
                call: retrofit2.Call<WeatherApp>,
                response: Response<WeatherApp>
            ) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val temperatureFloat = responseBody.main.temp
                    val temperatureInt = temperatureFloat.toInt()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val maxTempFloat = responseBody.main.temp_max
                    val maxTempInt = maxTempFloat.toInt()
                    val minTempFloat = responseBody.main.temp_min
                    val minTempInt = minTempFloat.toInt()
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    binding.temp.text = "$temperatureInt °C"
                    binding.weather.text = condition
                    binding.conditions.text = condition
                    binding.maxMinTemp.text = "$maxTempInt °C/$minTempInt °C"
                    binding.humidity.text = "$humidity %"
                    binding.wind.text = "$windSpeed m/s"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.location.text = "$cityName"

                    changeImageAccordingTOWeather(condition)
                }
            }

            override fun onFailure(call: retrofit2.Call<WeatherApp>, t: Throwable) {

            }

        } )
    }

    private fun changeImageAccordingTOWeather(condition: String) {
        when (condition){
            "partly Clouds","Clouds","Overcast","Mist","Foggy" -> {binding.root.setBackgroundResource((R.drawable.colud_background))
                      binding.lottieAnimationView.setAnimation(R.raw.cloud) }

            "Clear Sky","Sunny","Clear" -> {binding.root.setBackgroundResource((R.drawable.sunny_background))
                binding.lottieAnimationView.setAnimation(R.raw.sun) }

            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" -> {binding.root.setBackgroundResource((R.drawable.rain_background))
                binding.lottieAnimationView.setAnimation(R.raw.rain) }

            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" -> {binding.root.setBackgroundResource((R.drawable.snow_background))
                binding.lottieAnimationView.setAnimation(R.raw.snow) }

            else -> {binding.root.setBackgroundResource((R.drawable.sunny_background))
                binding.lottieAnimationView.setAnimation(R.raw.sun)}
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun time(timestamp : Long) : String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format((Date(timestamp*1000)))
    }
}