package com.namnp.aidl_ipc_client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val MUSIC_PACKAGE = "com.namnp.aidl_ipc_server"
    private val MUSIC_ACTION  = "com.namnp.aidl_ipc_server.sevice.MusicService.BIND"

    private var iMusicService: IMusicService? = null
    private var isServiceConnected: Boolean = false

    private val serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            iMusicService = IMusicService.Stub.asInterface(binder)
            isServiceConnected = true
            println("CONNECT")
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            iMusicService = null
            isServiceConnected = false
            println("DISCONNECT")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindService()
        initViews()
    }

    private fun initViews() {
        findViewById<Button>(R.id.button_play).setOnClickListener {
            if (!isServiceConnected) {
                return@setOnClickListener
            }
            try {
                iMusicService?.play()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        findViewById<Button>(R.id.button_pause).setOnClickListener {
            if (!isServiceConnected) {
                return@setOnClickListener
            }
            try {
                iMusicService?.pause()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private fun bindService() {
        val intent = Intent(MUSIC_ACTION).apply {
            `package` = MUSIC_PACKAGE
        }
        println("bindService")
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }


    override fun onDestroy() {
        unbindService(serviceConnection)
        super.onDestroy()
    }
}