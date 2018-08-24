package io.github.hidroh.splitme

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class SplitTileService : TileService() {
  override fun onTileAdded() {
    Log.d("tag", "added")
    with(qsTile) {
      state = Tile.STATE_INACTIVE
      updateTile()
    }
  }

  override fun onStartListening() {
    Log.d("tag", "start listening")
  }

  override fun onStopListening() {
    Log.d("tag", "stop listening")
  }

  override fun onClick() {
    Log.d("tag", "click")
    with(qsTile) {
      state = if (state == Tile.STATE_INACTIVE) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
      updateTile()
    }
    LocalBroadcastManager.getInstance(this)
        .sendBroadcast(Intent("io.github.hidroh.splitme.ACCESSIBILITY_ACTION"))
  }
}
