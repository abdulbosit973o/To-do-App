package uz.abdulbosit.apps.to_do_app

import android.app.Application
import ru.ifr0z.notify.data.room.AppDatabase
import ru.ifr0z.notify.data.source.local.impl.MySharedPrefImpl

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.init(this)
        MySharedPrefImpl.init(this)
    }
}