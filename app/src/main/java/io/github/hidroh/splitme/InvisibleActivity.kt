package io.github.hidroh.splitme

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class InvisibleActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    SplitTileService.splitScreen = isInMultiWindowMode
    LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(ACTION_CHECK_SPLIT_SCREEN))
    finish()
  }
}
