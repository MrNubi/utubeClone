package com.beyond.utubeclone.fragment

import android.content.Context
import android.net.Uri
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
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs

class PlayerFragment:  Fragment(R.layout.fragment_player) {

    private var binding : FragmentPlayerBinding? = null
    private lateinit var adapterVL : VidioRcAdapter
    private lateinit var player: SimpleExoPlayer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fb = FragmentPlayerBinding.bind(view)
        binding = fb
        initMotionLayout(fb)
        initRecyclerView(fb)
        initPlayerView(fb)
        initBottomPlaerControllBtn(fb)
        //binding 안쓰는 이유
        //지금은 앞에서 binding을 nullable하게 줘서, 로컬변수인 fb로 하는게 나음
        //왜냐하면 로컬변수는 not nullable하기 때문(nullable하면 null 처리를 해줘야됨 = 뭐같음)

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        player?.release()

    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    fun initBottomPlaerControllBtn(fb:FragmentPlayerBinding){
        fb.bottomPlayerControlButton.setOnClickListener {
            val player = this.player?: return@setOnClickListener
            // 엘비스 연산자로 null일경우 리턴으로 만들어줌
            if (player.isPlaying){
                player.pause()
            }
            else{player.play()}
        }
    }

    fun initPlayerView(fb:FragmentPlayerBinding){

        context?.let{
            player = SimpleExoPlayer.Builder(it).build()
            // ?.let로 context가 null일 가능성을 제거해서, it(Context)로 Buildr()안의 값을 채울 수 있음
        }
        fb.playerView.player= player

        binding?.let {
            player?.addListener(object : Player.EventListener{
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    // 플레잉 여부가 바뀔 때마다 들어오는 함수
                    if (isPlaying){
                        it.bottomPlayerControlButton.setImageResource(R.drawable.ic_baseline_pause_24)
                        //재생중 -> 버튼은 정지모양이어야 함

                    }
                    else{
                        // 정지중 -> 버튼은 재생모양이어야 한다
                        it.bottomPlayerControlButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)

                    }
                }
            })
        }


    }


    fun initRecyclerView(fb:FragmentPlayerBinding){
        adapterVL = VidioRcAdapter(callback = { url, title ->
            playVDOwithURL(url,title)

        })
        fb.fragmentRecyclerView.apply {
            getVidioListFromServer(context)
            adapter = adapterVL
            layoutManager = LinearLayoutManager(context)
        }
    }
    fun playVDOwithURL(url:String,title:String){
        context?.let{
            val dataSourceFactory = DefaultDataSourceFactory(it)
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
            player?.setMediaSource(mediaSource)
            player?.prepare()
            player?.play()
        }
        binding?.let {
            // ?.let{} -> let이 null이 아닐때만 동작하게
            it.playerMotionLayout.transitionToEnd()
            // 착 하고 열리면서 프레그먼트가 end상태가 됨
            it.bottomTitleTextView.text = title
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