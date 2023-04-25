package edu.temple.myapplication

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log

@Suppress("ControlFlowWithEmptyBody")
class TimerService : Service() {

    private var isRunning = false

    lateinit var t: TimerThread

    private var paused = false

    private var timerHandler: Handler? = null

    inner class TimerBinder : Binder() {

        // Check if Timer is already running
        var isRunning: Boolean
            get() = this@TimerService.isRunning
            set(value) {this@TimerService.isRunning = value}

        // Start a new timer
        fun start(startValue: Int){

            if (!paused) {
                if (!isRunning) {
                    if (::t.isInitialized) t.interrupt()
                    this@TimerService.start(startValue)
                }
            } else {
                pause()
            }
        }

        // Stop a currently running timer
        fun stop() {
            if (::t.isInitialized) {
                isRunning = false
                paused = false
                t.interrupt()
            }
        }

        // Pause a running timer
        fun pause() {
            this@TimerService.pause()
        }

        fun setHandler(handler: Handler) {
            this@TimerService.timerHandler = handler
        }

    }

    override fun onCreate() {
        super.onCreate()

        Log.d("TimerService status", "Created")
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d("TimerService status", "Bounded")
        return TimerBinder()
    }

    fun start(startValue: Int) {
        t = TimerThread(startValue)
        t.start()
        Log.d("Timer status", "Started")
    }

    fun pause () {
        if (::t.isInitialized) {
            paused = !paused
            isRunning = !paused
        }
    }

    inner class TimerThread(private val startValue: Int) : Thread() {

        override fun run() {
            isRunning = true
            try {
                for (i in startValue downTo 1)  {

                    sleep(1000)
                    while (paused);
                    Log.d("Countdown", i.toString())
                    timerHandler?.sendEmptyMessage(i)

                }
                isRunning = false
            } catch (e: InterruptedException) {
                Log.d("Timer status", "Stopped")
                isRunning = false
                paused = false
            }
        }

    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (::t.isInitialized) {
            t.interrupt()
        }

        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("TimerService status", "Destroyed")
    }


}