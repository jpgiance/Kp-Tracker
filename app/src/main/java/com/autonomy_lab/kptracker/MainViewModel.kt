package com.autonomy_lab.kptracker

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autonomy_lab.kptracker.data.DataStoreManager
import com.autonomy_lab.kptracker.utils.InternetConnectionObserver
import com.autonomy_lab.kptracker.data.models.PlanetaryKIndexItem
import com.autonomy_lab.kptracker.data.models.SettingsData
import com.autonomy_lab.kptracker.data.network.PlanetaryKIndexListSchema
import com.autonomy_lab.kptracker.data.network.toPlanetaryKIndexItemList
import com.autonomy_lab.kptracker.data.states_models.TopBarState
import com.autonomy_lab.kptracker.network.NoaaApi
import com.autonomy_lab.kptracker.notifications.NotificationProvider
import com.autonomy_lab.kptracker.ui.dialogs.SnackBarAction
import com.autonomy_lab.kptracker.ui.dialogs.SnackBarController
import com.autonomy_lab.kptracker.ui.dialogs.SnackBarEvent
import com.autonomy_lab.kptracker.ui.navigation.HelpAndFeedbackRoute
import com.autonomy_lab.kptracker.ui.navigation.MainScreenRoute
import com.autonomy_lab.kptracker.ui.navigation.RawDataScreenRoute
import com.autonomy_lab.kptracker.ui.navigation.SettingsScreenRoute
import com.autonomy_lab.kptracker.ui.widget.KpReceiverRepo
import com.autonomy_lab.kptracker.ui.widget.KpRepo
import com.autonomy_lab.kptracker.ui.widget.WidgetHelper
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MainViewModel @Inject constructor(
    private val noaaApi: NoaaApi,
    private val kpRepo: KpRepo,
    private val notificationProvider: NotificationProvider,
    private val dataStoreManager: DataStoreManager,
    internetConnectionObserver: InternetConnectionObserver
): ViewModel() {

    private var _latestKpValue = MutableStateFlow<Double?>(null)
    val latestKpValue: StateFlow<Double?> get() = _latestKpValue

    private var _latestKpValueTime = MutableStateFlow<String?>(null)
    val latestKpValueTime: StateFlow<String?> get() = _latestKpValueTime

    private var _kpIndexList = MutableStateFlow<List<PlanetaryKIndexItem>>(emptyList())
    val kpIndexList : StateFlow<List<PlanetaryKIndexItem>> get() = _kpIndexList

    private var _refreshing = MutableStateFlow(false)
    val refreshing: StateFlow<Boolean> get() = _refreshing

    private val _topBarState = MutableStateFlow(TopBarState())
    val topBarState: StateFlow<TopBarState> get() = _topBarState

    val settingsState: StateFlow<SettingsData> = dataStoreManager.settingsState

    val isInternetAvailable = internetConnectionObserver.isInternetAvailable.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        false
    )

    init {
        fetchKpIndexList()
    }

    companion object{
        var alreadyCheckedIfNotificationNeeded = false
    }

    fun fetchKpIndexList(){

       viewModelScope.launch{

            try {

                _refreshing.value = true

                val response = withContext(Dispatchers.IO){
                    noaaApi.fetchLastKpData()
                }

                if (response.isSuccessful){

                    val schema = response.body()?.let { PlanetaryKIndexListSchema(it) }

                    _kpIndexList.value = schema?.toPlanetaryKIndexItemList()?.sortedByDescending { it.timeTag }
                        ?: emptyList()


                    val latestItem: PlanetaryKIndexItem? = _kpIndexList.value.maxByOrNull { it.timeTag }

                    _latestKpValue.value = latestItem?.kpIndex
                    _latestKpValueTime.value = latestItem?.timeTag?.toStringForDisplay() ?: " --- "

                    KpReceiverRepo.updateKpIndexFromViewModel(latestItem?.kpIndex)
                    kpRepo.valueChanged()

                    checkIfNotificationIsNeeded()

//                    KpRepo.updateKpIndexFromViewModel(Random.nextDouble(90.0, 99.9))
//                    helper.updateKpWidget()

                }else{
                    Log.e("TAG", "doWork: Error Fetching data: Response Unsuccessful"  )
                }

                _refreshing.value = false

            }catch (e:Exception){
                Log.e("TAG", "doWork: Error Fetching data: \n ${e.message}"  )
                _refreshing.value = false
            }

        }

    }

    private fun checkIfNotificationIsNeeded() {

        if (settingsState.value.notificationsEnabled){
            latestKpValue.value.let { value ->
                if (value != null) {
                    if (value >= settingsState.value.notificationThreshold){
                        showSnackBar(message = "Notification Alert, Kp is $value", null)
                    }
                }
            }

        }
    }

    fun showSnackBar(message: String, action: SnackBarAction?) {

        val defaultAction = SnackBarAction(
            name = "Click me!",
            action = {
                SnackBarController.sendEvent(
                    event = SnackBarEvent(
                        message = "Action pressed!"
                    )
                )
            }
        )

        val defaultDismissAction = SnackBarAction(
            name = "Ok",
            action = {}
        )

        viewModelScope.launch {
            SnackBarController.sendEvent(
                event = SnackBarEvent(
                    message = message,
                    action = action ?: defaultDismissAction
                )
            )
        }
    }


    fun sendNotification(title: String, message: String){
        notificationProvider.sendNotification(title, message)
    }


    fun updateTopBar(route: String?) {
        _topBarState.value = when (route) {
            "com.autonomy_lab.kptracker.ui.navigation.MainScreenRoute" -> TopBarState(
                title = "Home",
                isRefreshIconVisible = true
            )
            "com.autonomy_lab.kptracker.ui.navigation.SettingsScreenRoute" -> TopBarState(
                title = "Settings",
                isRefreshIconVisible = false
            )
            "com.autonomy_lab.kptracker.ui.navigation.RawDataScreenRoute" -> TopBarState(
                title = "Raw Data",
                isRefreshIconVisible = true
            )
            "com.autonomy_lab.kptracker.ui.navigation.HelpAndFeedbackRoute" -> TopBarState(
                title = "Help And Feedback",
                isRefreshIconVisible = false
            )
            else -> TopBarState() // Default empty state
        }
    }


    fun showFeedBackDialog(context: Context, activity: Activity){


        if (settingsState.value.testInt < 60) return

        val manager = ReviewManagerFactory.create(context)

        try {
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // We got the ReviewInfo object
                    val reviewInfo = task.result

                    val flow = manager.launchReviewFlow(activity, reviewInfo)
                    flow.addOnCompleteListener { _ ->
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                    }
                } else {
                    // There was some problem, log or handle the error code.
                    @ReviewErrorCode val reviewErrorCode = (task.exception as ReviewException).errorCode
                }
            }
        }catch (_: Exception){

        }

    }

    fun test(){
        _latestKpValue.value = Random.nextDouble()
    }


}