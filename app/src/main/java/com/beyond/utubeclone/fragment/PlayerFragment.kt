package com.beyond.utubeclone.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyond.utubeclone.MainActivity
import com.beyond.utubeclone.R
import com.beyond.utubeclone.adapter.VidioRcAdapter
import com.beyond.utubeclone.databinding.FragmentPlayerBinding
import com.beyond.utubeclone.dto.VideoDto
import com.beyond.utubeclone.service.ServerAPI
import com.beyond.utubeclone.service.VidioService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs

class PlayerFragment:  Fragment(R.layout.fragment_player) {

    private var binding : FragmentPlayerBinding? = null
    private lateinit var adapterVL : VidioRcAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fb = FragmentPlayerBinding.bind(view)
        binding = fb
        initMotionLayout(fb)
        initRecyclerView(fb)
        //binding 안쓰는 이유
        //지금은 앞에서 binding을 nullable하게 줘서, 로컬변수인 fb로 하는게 나음
        //왜냐하면 로컬변수는 not nullable하기 때문(nullable하면 null 처리를 해줘야됨 = 뭐같음)

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
    fun initRecyclerView(fb:FragmentPlayerBinding){
        adapterVL = VidioRcAdapter()
        fb.fragmentRecyclerView.apply {
            getVidioListFromServer(context)
            adapter = adapterVL
            layoutManager = LinearLayoutManager(context)
        }
    }

    fun initMotionLayout(fb:FragmentPlayerBinding){
        fb.playerMotionLayout.setTransitionListener(object : MotionLayout.TransitionListener{
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {

            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                binding?.let{
                    // 실전에선 if문 걸어서, MainActivity가 아니면 실행하지 말라고 하는게 좋음
                    // 여기선 다른데가가 PlayerFragment 붙일 이유가 하등 없기에, 일단 조건문 안 만듦
                    (activity as MainActivity).also {
                        // also -> as 앞의 activity값을 MainActivity로 형변환
                        it.findViewById<MotionLayout>(R.id.mainMotionLayout).progress = abs(progress)
                        //progress -> 속성값 부여 하기 위해 씀, abs는 절댓값으로 치환 함수

                    }
                }
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {

            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {

            }
        })
    }//fun initMotionLayout
    fun getVidioListFromServer(context: Context){
        val retrofit = ServerAPI.getRetrofit(context)
        var apiList = retrofit.create(VidioService::class.java)
        apiList.getVidioList().enqueue(object : Callback<VideoDto> {
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