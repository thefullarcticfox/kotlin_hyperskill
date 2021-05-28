package com.thefullarcticfox.stopwatch

import android.app.*
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.fragment.app.DialogFragment
import java.lang.Exception

class Stopwatch(private val _textView: TextView,
                private val _runnable: Runnable,
                private val _interval: Long) {
    private inner class TimeUpdateRunnable : Runnable {
        override fun run() {
            if (_running) {
                _runnable.run()
                _textView.postDelayed(this, _interval)
            }
        }
    }
    private val _timeUpdateTask = TimeUpdateRunnable()
    private var _running = false

    fun start() {
        if (!_running) _textView.postDelayed(_timeUpdateTask, _interval)
        _running = true
    }

    fun stop() {
        _running = false
        _textView.removeCallbacks(_timeUpdateTask)
    }
}

class MainActivity : AppCompatActivity() {
    private var _passedSeconds = 0L
    private val _colors = arrayOf(
        Color.parseColor("#F44336"), Color.parseColor("#E91E63"),
        Color.parseColor("#9C27B0"), Color.parseColor("#3F51B5"),
        Color.parseColor("#2196F3"), Color.parseColor("#009688"),
        Color.parseColor("#4CAF50"), Color.parseColor("#CDDC39"),
        Color.parseColor("#FF6D00"), Color.parseColor("#546E7A")
    )
    private inner class TimerTaskRunnable : Runnable {
        override fun run() {
            _passedSeconds++; renderSeconds()
            // runOnUiThread { renderSeconds() }
        }
    }
    private val _timerTask = TimerTaskRunnable()
    private val _timerView: TextView by lazy {
        findViewById(R.id.textView)
    }
    private val _progressBar: ProgressBar by lazy {
        findViewById(R.id.progressBar)
    }
    private val _timer: Stopwatch by lazy {
        Stopwatch(_timerView, _timerTask, 1000L)
    }
    private val _settingsButton: Button by lazy {
        findViewById(R.id.settingsButton)
    }
    companion object {
        const val CHANNEL_ID = "com.thefullarcticfox"
        const val NOTIFICATION_ID = 424242
        var UPPER_LIMIT = Long.MAX_VALUE
        var OLD_COLORS: ColorStateList = ColorStateList.valueOf(Color.BLACK)
    }
    private var _notificationManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.startButton).setOnClickListener{ startTimer() }
        findViewById<Button>(R.id.resetButton).setOnClickListener{ resetTimer() }
        _settingsButton.setOnClickListener {
            SettingsDialogFragment().show(supportFragmentManager, SettingsDialogFragment.TAG)
        }
        OLD_COLORS = _timerView.textColors

        // Create the NotificationChannel
        val name = getString(R.string.app_name)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        mChannel.description = getString(R.string.channel_description)
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        _notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        _notificationManager!!.createNotificationChannel(mChannel)
    }

    private fun startTimer() {
        _settingsButton.isEnabled = false
        _progressBar.visibility = View.VISIBLE
        _timer.start()
    }

    private fun resetTimer() {
        _settingsButton.isEnabled = true
        _progressBar.visibility = View.INVISIBLE
        _timer.stop()
        _passedSeconds = 0
        _timerView.setTextColor(OLD_COLORS)
        renderSeconds()
        if (_notificationManager == null) return
        _notificationManager!!.cancelAll()
    }

    private fun fireNotification() {
        if (_notificationManager == null) return
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(applicationContext, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Notification")
            .setContentText("Time exceeded")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        _notificationManager!!.notify(NOTIFICATION_ID, builder.build())
    }

    private fun renderSeconds() {
        if (_passedSeconds > UPPER_LIMIT &&
            _timerView.textColors != ColorStateList.valueOf(Color.RED)) {
            _timerView.setTextColor(ColorStateList.valueOf(Color.RED))
            fireNotification()
        }
        _timerView.text = String.format("%02d:%02d", _passedSeconds / 60, _passedSeconds % 60)
        _progressBar.indeterminateTintList =
            ColorStateList.valueOf(_colors[_passedSeconds.toInt() % 10])
    }

    class SettingsDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
        {
            val builder = AlertDialog.Builder(requireContext())
            val view = layoutInflater.inflate(R.layout.settings_dialog, null)
            return builder.setTitle(getString(R.string.setUpperLimitInSeconds))
                .setView(view)
                .setNegativeButton(getString(R.string.cancel)) { _, _ -> dismiss() }
                .setPositiveButton(getString(R.string.ok)) { _,_ ->
                    UPPER_LIMIT = try {
                        view.findViewById<EditText>(R.id.upperLimitEditText)
                            .text!!.toString().toLong()
                    } catch (e: Exception) {
                        Long.MAX_VALUE
                    }
                }
                .create()
        }

        companion object {
            const val TAG = "SettingsDialog"
        }
    }
}
