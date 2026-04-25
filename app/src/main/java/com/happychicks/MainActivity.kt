package com.happychicks

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var eggCount = 0

    private lateinit var root: FrameLayout
    private lateinit var chickView: ImageView
    private lateinit var eggView: ImageView
    private lateinit var counterText: TextView

    private lateinit var soundPool: SoundPool
    private var laySoundId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        root = findViewById(R.id.root)
        chickView = findViewById(R.id.chick)
        eggView = findViewById(R.id.egg)
        counterText = findViewById(R.id.egg_counter)

        eggCount = prefs().getInt(KEY_EGG_COUNT, 0)
        updateCounter()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            ).build()
        laySoundId = soundPool.load(this, R.raw.sfx_fart, 1)

        // Position chick after the layout is measured
        root.post { placeChick(animate = false) }

        chickView.setOnClickListener { onChickTapped() }
    }

    private fun onChickTapped() {
        val oldX = chickView.x
        val oldY = chickView.y

        eggCount++
        updateCounter()
        prefs().edit().putInt(KEY_EGG_COUNT, eggCount).apply()

        soundPool.play(laySoundId, 1f, 1f, 1, 0, 1f)

        showEggAt(oldX, oldY)
        placeChickBesideEgg(eggX = oldX, eggY = oldY)
    }

    private fun updateCounter() {
        counterText.text = getString(R.string.egg_counter_label, eggCount)
    }

    /** Show the egg icon at the chick's old position, then fade it out. */
    private fun showEggAt(x: Float, y: Float) {
        eggView.x = x
        eggView.y = y
        eggView.alpha = 1f
        eggView.visibility = View.VISIBLE
        eggView.animate()
            .alpha(0f)
            .setDuration(700)
            .withEndAction { eggView.visibility = View.GONE }
            .start()
    }

    /** Move the chick to sit just beside the egg it just laid. */
    private fun placeChickBesideEgg(eggX: Float, eggY: Float) {
        val eggW = dpToPx(70f)   // ic_egg width in dp
        val gap  = dpToPx(8f)

        // Prefer moving to the right; if that clips out of screen, go left
        val rightX = eggX + eggW + gap
        val leftX  = eggX - chickView.width - gap

        val newX = when {
            rightX + chickView.width <= root.width -> rightX
            leftX >= 0 -> leftX
            else -> (root.width - chickView.width).coerceAtLeast(0).toFloat()
        }
        val newY = eggY.coerceIn(0f, (root.height - chickView.height).toFloat().coerceAtLeast(0f))

        chickView.animate()
            .x(newX).y(newY)
            .setDuration(280)
            .setInterpolator(OvershootInterpolator(1.8f))
            .start()
    }

    /** Move the chick to a random position (used on first placement). */
    private fun placeChick(animate: Boolean) {
        val maxX = (root.width - chickView.width).coerceAtLeast(0)
        val maxY = (root.height - chickView.height).coerceAtLeast(0)
        val newX = if (maxX > 0) Random.nextInt(maxX).toFloat() else 0f
        val newY = if (maxY > 0) Random.nextInt(maxY).toFloat() else 0f

        if (animate) {
            chickView.animate()
                .x(newX).y(newY)
                .setDuration(280)
                .setInterpolator(OvershootInterpolator(1.8f))
                .start()
        } else {
            chickView.x = newX
            chickView.y = newY
        }
    }

    private fun dpToPx(dp: Float) = dp * resources.displayMetrics.density

    private fun prefs() = getSharedPreferences("game", Context.MODE_PRIVATE)

    override fun onDestroy() {
        soundPool.release()
        super.onDestroy()
    }

    companion object {
        private const val KEY_EGG_COUNT = "egg_count"
    }
}
