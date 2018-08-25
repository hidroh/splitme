package io.github.hidroh.splitme

import android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_GENERIC
import android.content.*
import android.content.Intent.*
import android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS
import android.service.quicksettings.Tile.STATE_ACTIVE
import android.service.quicksettings.Tile.STATE_INACTIVE
import android.service.quicksettings.TileService
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class SplitTileService : TileService() {
  private val localBroadcastManager
    get() = LocalBroadcastManager.getInstance(this)
  private val receiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      updateTileState()
    }
  }

  override fun onStartListening() {
    localBroadcastManager.registerReceiver(receiver, IntentFilter(ACTION_CHECK_SPLIT_SCREEN))
    checkSplitScreen()
  }

  override fun onStopListening() {
    localBroadcastManager.unregisterReceiver(receiver)
  }

  override fun onClick() {
    if (!enabled()) {
      prompt()
    } else {
      toggle()
      updateTileState()
    }
  }

  private fun toggle() {
    localBroadcastManager.sendBroadcast(Intent(ACTION_TOGGLE_SPLIT_SCREEN))
    splitScreen = !splitScreen
  }

  private fun updateTileState() {
    qsTile?.apply {
      state = if (splitScreen) STATE_ACTIVE else STATE_INACTIVE
      updateTile()
    }
  }

  private fun checkSplitScreen() {
    startActivity(Intent(this, InvisibleActivity::class.java)
        .setFlags(FLAG_ACTIVITY_LAUNCH_ADJACENT or FLAG_ACTIVITY_NEW_TASK))
  }

  private fun enabled(): Boolean {
    val manager = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
    if (!manager.isEnabled) return false
    val serviceId = ComponentName(this, SplitToggleService::class.java).flattenToShortString()
    manager.getEnabledAccessibilityServiceList(FEEDBACK_GENERIC)
        .forEach { if (it.id == serviceId) return true }
    return false
  }

  private fun prompt() {
    showDialog(AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
        .setMessage(R.string.accessibility_required)
        .setNegativeButton(android.R.string.cancel, null)
        .setPositiveButton(android.R.string.ok) { _, _ ->
          startActivity(Intent(ACTION_ACCESSIBILITY_SETTINGS)
              .addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_NO_HISTORY))
        }
        .create())
  }

  companion object {
    var splitScreen: Boolean = false
  }
}
