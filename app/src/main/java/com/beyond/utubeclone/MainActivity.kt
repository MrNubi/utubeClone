package com.beyond.utubeclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyond.utubeclone.adapter.VidioRcAdapter
import com.beyond.utubeclone.dto.VideoDto
import com.beyond.utubeclone.fragment.PlayerFragment
import com.beyond.utubeclone.service.ServerAPI
import com.beyond.utubeclone.service.VidioService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var adapterVL : VidioRcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PlayerFragment())
            .commit()
        adapterVL = VidioRcAdapter()
        val Rv = findViewById<RecyclerView>(R.id.mainRecyclerView)
        Rv.adapter = adapterVL
        Rv.layoutManager = LinearLayoutManager(this)



//        findViewById<RecyclerView>(R.id.mainRecyclerView).apply {
//            adapter = adapterVL
//            layoutManager = LinearLayoutManager(context)
//        }

        getVidioListFromServer()
    }

    fun getVidioListFromServer(){
        val retrofit = ServerAPI.getRetrofit(this)
        var apiList = retrofit.create(VidioService::class.java)
        apiList.getVidioList().enqueue(object :Callback<VideoDto>{
            override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
                if(response.isSuccessful.not()){
                    Log.d("리스폰 꼬임", "Fail")
                    return
                }

                response.body()?.let {
                    Log.d("리스폰 잘됨", "바디 = "+it.toString())
                    adapterVL.submitList(it.videos)
                    adapterVL.notifyDataSetChanged()
                    Log.d("섭밋리스트", adapterVL.currentList.toString())
                }
            }

            override fun onFailure(call: Call<VideoDto>, t: Throwable) {
                //예외처리 ZONE
            }
        })
    }
}