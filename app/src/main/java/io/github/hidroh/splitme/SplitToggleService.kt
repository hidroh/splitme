package io.github.hidroh.splitme

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class SplitToggleService : AccessibilityService() {
  private val receiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      Log.d("tag", "toggle")
      performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)
    }
  }

  override fun onCreate() {
    Log.d("tag", "listen")
    LocalBroadcastManager.getInstance(this)
        .registerReceiver(receiver, IntentFilter("io.github.hidroh.splitme.ACCESSIBILITY_ACTION"))
  }

  override fun onServiceConnected() {
    Log.d("tag", "connected")
  }

  override fun onInterrupt() { }

  override fun onAccessibilityEvent(event: AccessibilityEvent) { }

  override fun onDestroy() {
    Log.d("tag", "sleep")
    LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
  }
}
