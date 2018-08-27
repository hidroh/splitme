package io.github.hidroh.splitme

import android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_GENERIC
import android.app.AlertDialog
import android.content.*
import android.content.Intent.*
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.graphics.drawable.Icon
import android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS
import android.service.quicksettings.Tile.STATE_ACTIVE
import android.service.quicksettings.Tile.STATE_INACTIVE
import android.service.quicksettings.TileService
import android.view.accessibility.AccessibilityManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class SplitTileService : TileService() {
  private val localBroadcastManager
    get() = LocalBroadcastManager.getInstance(this)
  private val receiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      updateTileState(intent.getBooleanExtra(EXTRA_IS_IN_SPLIT_SCREEN, false))
    }
  }
  private var isActive: Boolean = false
  private var iconResId: Int = R.drawable.ic_split_black_24dp

  override fun onStartListening() {
    localBroadcastManager.registerReceiver(receiver, IntentFilter(ACTION_SPLIT_SCREEN_CHECKED))
    updateIcon(resources.configuration)
    checkSplitScreen()
  }

  override fun onStopListening() {
    localBroadcastManager.unregisterReceiver(receiver)
  }

  override fun onClick() {
    if (enabled()) {
      updateTileState(!isActive)
      toggleAndCollapse()
    } else {
      prompt()
    }
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    updateIcon(newConfig)
    updateTileState(isActive)
  }

  private fun toggleAndCollapse() {
    startActivityAndCollapse(Intent(this, InvisibleActivity::class.java)
        .setAction(ACTION_TOGGLE_SPLIT_SCREEN)
        .setFlags(FLAG_ACTIVITY_NEW_TASK))
  }

  private fun updateTileState(active: Boolean) {
    isActive = active
    qsTile?.apply {
      state = if (isActive) STATE_ACTIVE else STATE_INACTIVE
      label = getString(if (isActive) R.string.label_on else R.string.label_off)
      icon = Icon.createWithResource(this@SplitTileService, iconResId)
      updateTile()
    }
  }

  private fun checkSplitScreen() {
    startActivity(Intent(this, InvisibleActivity::class.java)
        .setAction(ACTION_CHECK_SPLIT_SCREEN)
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
    showDialog(AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
        .setMessage(R.string.accessibility_required)
        .setNegativeButton(android.R.string.cancel, null)
        .setPositiveButton(android.R.string.ok) { _, _ ->
          startActivity(Intent(ACTION_ACCESSIBILITY_SETTINGS)
              .addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_NO_HISTORY))
        }
        .create())
  }

  private fun updateIcon(newConfig: Configuration) {
    iconResId = if (newConfig.orientation == ORIENTATION_LANDSCAPE) {
      R.drawable.ic_split_land_black_24dp
    } else {
      R.drawable.ic_split_black_24dp
    }
  }
}
