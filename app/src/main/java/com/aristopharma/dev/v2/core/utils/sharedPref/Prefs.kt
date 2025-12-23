package com.aristopharma.dev.v2.core.utils.sharedPref


import android.content.Context
import android.content.SharedPreferences
import android.content.SyncInfo
import com.aristopharma.dev.v2.features.dashboard.domain.model.DashboardSummary
import com.aristopharma.dev.v2.features.dashboard.domain.model.AttendanceSession
import com.aristopharma.dev.v2.features.dashboard.domain.model.AttendanceModel
import com.aristopharma.dev.v2.features.dashboard.domain.model.EmployeeInfo
import com.aristopharma.dev.v2.features.dashboard.domain.model.PostOrderInfo
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.aristopharma.dev.v2.features.login.data.model.LoginModel

/**
 * How to use?
 *
 * Scenario 1: Raw string/int/double
 * Declare Variable > make get() = preferences.getString(TAG, default) and set(TAG)
 * Read data like prefs.myVar, set data prefs.myVar = "abc"
 *
 * Scenario 2: Model / Object
 * Declare Variable along with default value >
 * get() = get<Model>(::myVar.name) ?: Model()
 * set(value) = put(::myVar.name, value)
 *
 */

class Prefs (context: Context) {
    private val SHARED_PREF_NAME = "ARISTOPHARMA_SHARED_PREFERENCE"
    val preferences: SharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    val gson: Gson = GsonBuilder().create()

    init {

    }

    var hasPendingOrderApprovalRedDot:Boolean
        get() = preferences.getBoolean(HAS_PENDING_ORDER, false)
        set(value) = write(HAS_PENDING_ORDER, value)

    var empId:String?
        get() = preferences.getString(EMP_ID, "")
        set(value) = write(EMP_ID, value!!)


    /* model collection */

    var loginModel: LoginModel
        get() = get<LoginModel>(::loginModel.name) ?: LoginModel()
        set(value) = put(::loginModel.name, value)

    var employeeInfo: EmployeeInfo
        get() = get<EmployeeInfo>(::employeeInfo.name) ?: EmployeeInfo()
        set(value) = put(::employeeInfo.name, value)


    var syncInfo: Any? // Changed to Any? to avoid android.content.SyncInfo conflict if not intended
        get() = get<Any>(::syncInfo.name)
        set(value) = put(::syncInfo.name, value)

/*    var mobileServer: MobileServer
        get() = get<MobileServer>(::mobileServer.name) ?: MobileServer()
        set(value) = put(::mobileServer.name, value)
        */

    var dashboardSummaryModel: DashboardSummary
        get() = get<DashboardSummary>(::dashboardSummaryModel.name) ?: DashboardSummary(
            employeeName = "",
            employeeId = "",
            attendanceStatus = "",
            lastSyncTime = "",
            isFirstSyncDone = false
        )
        set(value) = put(::dashboardSummaryModel.name, value)

    var attendanceModel: AttendanceModel
        get() = get<AttendanceModel>(::attendanceModel.name) ?: AttendanceModel()
        set(value) = put(::attendanceModel.name, value)


    var postOrderInfo: PostOrderInfo
        get() = get<PostOrderInfo>(::postOrderInfo.name) ?: PostOrderInfo()
        set(value) = put(::postOrderInfo.name, value)

/*
    var attendanceModel:AttendanceModel
        get() = get<AttendanceModel>(::attendanceModel.name) ?: AttendanceModel()
        set(value) = put(::attendanceModel.name, value)


    var postOrderInfo:PostOrderInfo
        get() = get<PostOrderInfo>(::postOrderInfo.name) ?: PostOrderInfo()
        set(value) = put(::postOrderInfo.name, value)*/


    fun <T> put(key: String, `object`: T) {
        val jsonString = gson.toJson(`object`)
        preferences.edit().putString(key, jsonString).apply()
    }

    inline fun <reified T> get(key: String): T? {
        val value = preferences.getString(key, null)
        return if(value != null) gson.fromJson(value, T::class.java) else null
    }


    private fun write(key:String, value:String){ preferences.edit().putString(key, value).apply() }
    private fun write(key:String, value:Int){ preferences.edit().putInt(key, value).apply() }
    private fun write(key:String, value:Boolean){ preferences.edit().putBoolean(key, value).apply() }
    private fun write(key:String, value:Float){ preferences.edit().putFloat(key, value).apply() }
    private fun writeModel(key: String, model: Any){ preferences.edit().putString(key, gson.toJson(model)).apply()  }


    companion object{
        private val APP_PREF_INT = "intPref"
        private val APP_PREF_STRING= "stringPref"
        private val APP_PREF_BOOL = "boolPref"
        private val APP_PREF_DOUBLE = "doublePref"

        private val EMP_ID = "EMP_ID"
        private val DAY_RUNNING_MODEL = "DAY_RUNNING_MODEL"
        private val END_DAY_MODEL = "END_DAY_MODEL"


        private val ACCESS_TOKEN = "ACCESS_TOKEN"
        private val REFRESH_TOKEN = "REFRESH_TOKEN"


        private val IS_LOGGED_IN = "IS_LOGGED_IN"
        private val IS_DEVICE_LOGGED_IN = "IS_DEVICE_LOGGED_IN"
        private val HAS_PENDING_ORDER = "HAS_PENDING_ORDER"
        private val IS_FIRST_SYNC_DONE = "IS_FIRST_SYNC_DONE"

        private val POST_ORDER_COUNT = "POST_ORDER_COUNT"


    }


}