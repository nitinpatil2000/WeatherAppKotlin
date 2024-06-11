package com.courses.wheatherapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.courses.wheatherapp.R
import com.courses.wheatherapp.databinding.FragmentMainBinding
import com.courses.wheatherapp.util.ErrorHandling
import com.courses.wheatherapp.viewmodel.WeatherViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<WeatherViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchWeatherData("Dhule")
        searchCityWeatherData()
    }

    private fun searchCityWeatherData() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    validateCityName(query)
                }

                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    private fun validateCityName(cityName: String) {
        viewModel.validateCityName(cityName, "metric").observe(viewLifecycleOwner) { isValid ->
            if (isValid) {
                fetchWeatherData(cityName)
            } else {
                Snackbar.make(binding.root, "City not found", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchWeatherData(cityName: String) {

        viewModel.fetchWeatherData(cityName, "metric")

        viewModel.weatherData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ErrorHandling.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is ErrorHandling.Error -> {
                    binding.progressBar.visibility = View.GONE
                    val errorMessage = state.message ?: "An unknown error occurred"
                    Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                }

                is ErrorHandling.Success -> {
                    binding.progressBar.visibility = View.GONE
                    state.data?.let { wheather ->
                        binding.apply {
                            val condition = wheather.weather.firstOrNull()?.main ?: "Unknown"
                            val sunset = wheather.sys.sunset.toLong()
                            val sunrise = wheather.sys.sunrise.toLong()

                            tempratureTxt.text = "${wheather.main.temp}°C"
                            txtHumidity.text = "${wheather.main.humidity} %"
                            txtWind.text = "${wheather.wind.speed} m/s"
                            txtSunrise.text = time(sunrise)
                            txtSunset.text = time(sunset)
                            txtSea.text = "${wheather.main.pressure} hPa"
                            tempName.text = condition
                            txtCondition.text = condition
                            minTempTxt.text = "Max Temp: ${wheather.main.temp_min}°C"
                            maxTempTxt.text = "Min Temp: ${wheather.main.temp_max}°C"

                            dayName.text = dayName(System.currentTimeMillis())
                            dayDateTxt.text = date()
                            locationTxt.text = cityName

                            changeImgInCondition(condition)
                        }
                    }
                }
            }
        }

    }


    private fun dayName(timeStamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date(timeStamp))
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun time(timeStamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm", Locale.getDefault())
        return sdf.format((Date(timeStamp*1000)))
    }

    private fun changeImgInCondition(condition: String) {
        when (condition) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.apply {
                    root.setBackgroundResource(R.drawable.sunny_background)
                    tempLottieView.setAnimation(R.raw.sun)
                }
            }

            "Partly Cloud", "Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.apply {
                    root.setBackgroundResource(R.drawable.colud_background)
                    tempLottieView.setAnimation(R.raw.cloud)
                }
            }


            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.apply {
                    root.setBackgroundResource(R.drawable.rain_background)
                    tempLottieView.setAnimation(R.raw.rain)
                }
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.apply {
                    root.setBackgroundResource(R.drawable.snow_background)
                    tempLottieView.setAnimation(R.raw.snow)
                }
            }

            else -> {
                binding.apply {
                    root.setBackgroundResource(R.drawable.sunny_background)
                    tempLottieView.setAnimation(R.raw.sun)
                }
            }
        }
        binding.tempLottieView.playAnimation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}