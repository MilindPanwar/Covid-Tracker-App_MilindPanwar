package com.example.covidtracker
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Request.*
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.w3c.dom.Text
import java.util.*

import kotlin.collections.ArrayList
import com.android.volley.Request.Method as RequestMethod

class MainActivity : AppCompatActivity() {
    lateinit var worldCasesTV: TextView
    lateinit var worldRecoveredTV: TextView
    lateinit var worldDeceasedTV: TextView
    lateinit var countryRecoveredTV: TextView
    lateinit var countryCasesTV: TextView
    lateinit var countryDeceasedTV: TextView
    lateinit var stateRV: RecyclerView
    lateinit var stateRVAdapter: StateRVAdapter
    lateinit var stateList: List<StateModal>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        worldCasesTV =findViewById(R.id.idTVWorldCases)
        worldRecoveredTV =findViewById(R.id.idTVWorldRecovered)
        worldDeceasedTV =findViewById(R.id.idTVWorldDeceased)
        countryCasesTV =findViewById(R.id.idTVCases)
        countryRecoveredTV =findViewById(R.id.idTVRecovered)
        countryDeceasedTV =findViewById(R.id.idTVDeceased)
        stateRV=findViewById(R.id.idRVStates)
        stateList= ArrayList<StateModal>()
        getStateInfo()
        getWorldInfo()
    }
    private fun getStateInfo(){
            val queue = Volley.newRequestQueue(this@MainActivity)
            val url = "https://api.rootnet.in/covid19-in/stats/latest"

            val request =
                JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    try{
                        val dataObj= response.getJSONObject("data")
                        val summaryObj = dataObj.getJSONObject("summary")
                        val cases:Int = summaryObj.getInt("total")
                        val recovered:Int=summaryObj.getInt("discharged")
                        val deceased:Int=summaryObj.getInt("deaths")

                        countryCasesTV.text=cases.toString()
                        countryRecoveredTV.text=recovered.toString()
                        countryDeceasedTV.text=deceased.toString()

                        val regionalArray =dataObj.getJSONArray("regional")
                        for(i in 0 until regionalArray.length()){
                            val regionalObj=regionalArray.getJSONObject(i)
                            val stateName : String = regionalObj.getString("log")
                            val cases : Int = regionalObj.getInt("totalConfirmed")
                            val deceased : Int = regionalObj.getInt("deaths")
                            val recovered : Int = regionalObj.getInt("discharged")

                            val stateModal = StateModal(stateName,recovered,deceased,cases)
                            stateList=stateList+stateModal
                        }
                       stateRVAdapter= StateRVAdapter(stateList)
                        stateRV.layoutManager=LinearLayoutManager(this)
                        stateRV.adapter=stateRVAdapter


                    }catch(e:JSONException){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, "fail to get", Toast.LENGTH_SHORT).show()
                })
            queue.add(request)

// Access the RequestQueue through your singleton class.
            //MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }


    private fun getWorldInfo(){
        val url ="https://corona.lmao.ninja/v3/covid-19/all"
        val queue = Volley.newRequestQueue(this@MainActivity)
        val request =
            JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val worldCases: Int = response.getInt("cases")
                    val worldRecoverd: Int = response.getInt("recovered")
                    val worldDeceased: Int = response.getInt("deaths")
                    worldRecoveredTV.text = worldCases.toString()
                    worldDeceasedTV.text = worldCases.toString()
                    worldCasesTV.text = worldCases.toString()
                }catch(e:JSONException){
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                    Toast.makeText(this, "fail to get", Toast.LENGTH_SHORT).show()
                })
        queue.add(request)


    }

}