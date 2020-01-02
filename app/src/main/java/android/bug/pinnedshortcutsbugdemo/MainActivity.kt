package android.bug.pinnedshortcutsbugdemo

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.ShortcutManager
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        bugDescription.isClickable = true
        bugDescription.movementMethod = LinkMovementMethod.getInstance()
        bugDescription.text = Html.fromHtml(BUG_DESCRIPTION_HTML)

        queryShortcuts.setOnClickListener {
            operateShortcutsIfSupportedByLauncher {
                val shortcutsCurrentlyPinned =
                    getSystemService(ShortcutManager::class.java)!!.pinnedShortcuts.size
                shortcutsPinnedText.text = "Shortcuts currently pinned: $shortcutsCurrentlyPinned"

                Toast.makeText(applicationContext, "Number of pinned shortcuts updated", Toast.LENGTH_SHORT).show()
            }
        }

        addShortcuts.setOnClickListener {
            operateShortcutsIfSupportedByLauncher {
                addPinnedShortcut()
            }
        }
    }

    private fun operateShortcutsIfSupportedByLauncher(block: () -> Unit): Unit {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(applicationContext)) {
            block()
        } else {
            Toast.makeText(applicationContext, "Your launcher does not support pinned shortcuts", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addPinnedShortcut(shortcutTitle: String = "test") {
        val shortcutInfo = ShortcutInfoCompat.Builder(applicationContext, shortcutTitle)
            .setIntent(packageManager.getLaunchIntentForPackage(packageName)!!)
            .setShortLabel(shortcutTitle)
            .setIcon(IconCompat.createWithResource(applicationContext, R.mipmap.ic_launcher))
            .build()

        val launcherActivity = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val callback = PendingIntent
            .getActivity(applicationContext, 0, launcherActivity, PendingIntent.FLAG_UPDATE_CURRENT)
            .intentSender
        ShortcutManagerCompat.requestPinShortcut(applicationContext, shortcutInfo,  callback)
    }

    private companion object {
        const val PINNED_SHORTCUTS_URL =
            "https://developer.android.com/reference/android/content/pm/ShortcutManager.html#getPinnedShortcuts()"
        const val BUG_DESCRIPTION_HTML =
            "On Android API 25 devices<br><a href='$PINNED_SHORTCUTS_URL'>ShortcutManager.getPinnedShortcuts()</a><br>always incorrectly returns 0 items"
    }
}
