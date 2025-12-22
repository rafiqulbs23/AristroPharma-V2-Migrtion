package com.aristopharma.dev.v2.shared.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aristopharma.dev.v2.features.login.data.dataSource.local.LoginDao
import com.aristopharma.dev.v2.features.login.data.dataSource.local.LoginEntity


@Database(
    entities = [
        PostEntity::class,
        LoginEntity::class
    ],
    version = 1,
    exportSchema = false
)

/*@TypeConverters(
    ProductWrapperConverters::class,
                PrescriptionSurveyWrapper::class
    )*/
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun loginDao(): LoginDao
}