package com.androidproject.data

import com.androidproject.data.local.Session
import com.androidproject.data.local.SessionStorage

// In-memory replacement for the Keystore storage, which only works on a real device
class FakeSessionStorage(var saved: Session? = null) : SessionStorage {
    override fun save(session: Session) {
        saved = session
    }

    override fun load(): Session? = saved

    override fun clear() {
        saved = null
    }
}
