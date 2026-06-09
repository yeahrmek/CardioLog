package com.cardiolog.app

import android.app.Application
import com.cardiolog.app.data.BloodPressureRepository
import com.cardiolog.app.data.CardioLogDatabase

class CardioLogApplication : Application() {
    val repository: BloodPressureRepository by lazy {
        BloodPressureRepository(CardioLogDatabase.getInstance(this).bloodPressureDao())
    }
}
