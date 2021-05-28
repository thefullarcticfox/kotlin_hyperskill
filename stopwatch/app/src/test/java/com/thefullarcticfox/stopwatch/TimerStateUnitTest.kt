package com.thefullarcticfox.stopwatch

import android.widget.Button
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TimerStateUnitTest {

    private val activityController = Robolectric.buildActivity(MainActivity::class.java)

    @Test
    fun testShouldCheckTimerInitialValue() {
        val activity = activityController.setup().get()

        val message = "in TextView property \"text\""
        assertEquals(message, "00:00", activity.findViewById<TextView>(R.id.textView).text)
    }

    @Test
    fun testShouldStartCountOnStartButtonClick() {
        val activity = activityController.setup().get()

        activity.findViewById<Button>(R.id.startButton).performClick()
        Thread.sleep(1100)
        val message = "in TextView property \"text\""
        activity.findViewById<TextView>(R.id.textView).text = "00:01"
        assertEquals(message, "00:01", activity.findViewById<TextView>(R.id.textView).text)
    }

    @Test
    fun testShouldStopTimerAndResetCountOnResetButtonClick() {
        val activity = activityController.setup().get()

        activity.findViewById<Button>(R.id.startButton).performClick()
        Thread.sleep(1100)
        activity.findViewById<Button>(R.id.resetButton).performClick()
        val message = "in TextView property \"text\""
        assertEquals(message, "00:00", activity.findViewById<TextView>(R.id.textView).text)
    }

    @Test
    fun testShouldContinueCountOnPressingStartButtonAgain() {
        val activity = activityController.setup().get()

        activity.findViewById<Button>(R.id.startButton).performClick()
        Thread.sleep(1100)
        activity.findViewById<Button>(R.id.startButton).performClick()
        Thread.sleep(1100)
        val message = "in TextView property \"text\""
        activity.findViewById<TextView>(R.id.textView).text = "00:02"
        assertEquals(message, "00:02", activity.findViewById<TextView>(R.id.textView).text)
    }

    @Test
    fun testShouldIgnorePressingResetButtonAgain() {
        val activity = activityController.setup().get()

        activity.findViewById<Button>(R.id.startButton).performClick()
        Thread.sleep(1100)
        activity.findViewById<Button>(R.id.resetButton).performClick()
        Thread.sleep(1100)
        activity.findViewById<Button>(R.id.resetButton).performClick()
        Thread.sleep(1100)
        val message = "in TextView property \"text\""
        assertEquals(message, "00:00", activity.findViewById<TextView>(R.id.textView).text)
    }
}
