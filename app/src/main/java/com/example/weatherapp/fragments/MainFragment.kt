package com.example.weatherapp.fragments

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.R
import com.example.weatherapp.adapters.VPAdapter
import com.example.weatherapp.adapters.WeatherModel
import com.example.weatherapp.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONObject


class MainFragment : Fragment() {
    private val fList = listOf(HoursFragment.newInstance(), DaysFragment.newInstance())
    private val tList = listOf("Hours", "Days")

    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        init()
        requestWeatherData("berezino,belarus")
    }

    private fun init() = with(binding) {
        val adapter = VPAdapter(activity as FragmentActivity, fList)
        vp.adapter = adapter
        TabLayoutMediator(tabLayout, vp) { tab, position ->
            tab.text = tList[position]
        }.attach()

    }


    private fun permissionListener() {
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestWeatherData(city: String) {
        var url =
            "https://api.weatherapi.com/v1/forecast.json?key=c042dd8c71a84cdda52111027240808&q=${city}&days=10&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(Request.Method.GET, url, { result ->
            parseWeatherData(result)
        }, { error ->
            Log.d("MyLog", error.toString())
        })
        queue.add(request)

    }


    private fun parseWeatherData(result: String) {
        val mainObject = JSONObject(result)
        val item = WeatherModel(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            mainObject.getJSONObject("current").getString("temp_c"),
            "",
            "",
            ""

        )

        Log.d("MyLog", item.toString())


    }


    companion object {

        @JvmStatic
        fun newInstance() = MainFragment()

    }
}