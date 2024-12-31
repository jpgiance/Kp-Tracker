package com.autonomy_lab.kptracker.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autonomy_lab.kptracker.Utils.InternetConnectionObserver
import com.autonomy_lab.kptracker.data.PlanetaryKIndexItem
import com.autonomy_lab.kptracker.data.network.PlanetaryKIndexListSchema
import com.autonomy_lab.kptracker.data.network.toPlanetaryKIndexItemList
import com.autonomy_lab.kptracker.network.NoaaApi
import com.autonomy_lab.kptracker.ui.widget.KpRepo
import com.autonomy_lab.kptracker.ui.widget.WidgetHelper
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
class KpTrackerWidgetViewModel @Inject constructor(
    private val noaaApi: NoaaApi,
    private val helper: WidgetHelper,
    internetConnectionObserver: InternetConnectionObserver
): ViewModel() {

    private var _latestKpValue = MutableStateFlow<Double?>(null)
    val latestKpValue: StateFlow<Double?> get() = _latestKpValue

    private var _kpIndexList = MutableStateFlow<List<PlanetaryKIndexItem>>(emptyList())
    val kpIndexList : StateFlow<List<PlanetaryKIndexItem>> get() = _kpIndexList

    private var _refreshing = MutableStateFlow<Boolean>(false)
    val refreshing: StateFlow<Boolean> get() = _refreshing

    val isInternetAvailable = internetConnectionObserver.isInternetAvailable.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        false
    )

    init {
        fetchKpIndexList()
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

                    KpRepo.updateKpIndexFromViewModel(latestItem?.kpIndex)
//                    KpRepo.updateKpIndexFromViewModel(Random.nextDouble(90.0, 99.9))
                    helper.updateKpWidget()

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

    fun test(){
        _latestKpValue.value = Random.nextDouble()
    }


}