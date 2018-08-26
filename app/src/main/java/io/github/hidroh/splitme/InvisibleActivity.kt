package io.github.hidroh.splitme

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class InvisibleActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val broadcastManager = LocalBroadcastManager.getInstance(this)
    when(intent.action) {
      ACTION_CHECK_SPLIT_SCREEN -> broadcastManager.sendBroadcast(Intent(ACTION_SPLIT_SCREEN_CHECKED)
          .putExtra(EXTRA_IS_IN_SPLIT_SCREEN, isInMultiWindowMode))
      ACTION_TOGGLE_SPLIT_SCREEN -> broadcastManager.sendBroadcast(Intent(ACTION_TOGGLE_SPLIT_SCREEN))
    }
    finish()
  }
}
