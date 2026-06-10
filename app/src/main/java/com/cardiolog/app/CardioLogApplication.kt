package com.cardiolog.app

import android.app.Application
import com.cardiolog.app.data.BloodPressureRepository
import com.cardiolog.app.data.CardioLogDatabase
import com.cardiolog.app.data.UserProfileRepository

class CardioLogApplication : Application() {
    private val database: CardioLogDatabase by lazy { CardioLogDatabase.getInstance(this) }

    val repository: BloodPressureRepository by lazy {
        BloodPressureRepository(database.bloodPressureDao())
    }

    val profileRepository: UserProfileRepository by lazy {
        UserProfileRepository(database.userProfileDao())
    }
}
