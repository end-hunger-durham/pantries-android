package org.endhungerdurham.pantries.ui.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.serialization.json.JSON
import org.endhungerdurham.pantries.Pantry
import org.endhungerdurham.pantries.PantryList
import java.io.IOException
import java.net.URL

enum class NetworkState {
    SUCCESS,
    LOADING,
    FAILURE,
}

class PantriesViewModel: ViewModel() {
    private var pantriesRepo: List<Pantry> = emptyList()
    private val query = MutableLiveData<String>()
    private val mutableNetworkState: MutableLiveData<NetworkState> = MutableLiveData()
    val networkState: LiveData<NetworkState> = mutableNetworkState
    val pantries: LiveData<List<Pantry>> = Transformations.switchMap(query) { query ->
        val filtered = MutableLiveData<List<Pantry>>()
        filtered.value = when (query) {
            "" -> pantriesRepo
            else -> {
                pantriesRepo.filter { pantry ->
                    with(pantry) {
                        false.takeUnless { organizations.contains(query, ignoreCase = true) }
                                ?.takeUnless { address.contains(query, ignoreCase = true) }
                                ?.takeUnless { city.contains(query, ignoreCase = true) } ?: true
                    }
                }
            }
        }
        filtered
    }

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        query.value = ""
        reloadPantries()
    }

    private suspend fun fetchPantries(): List<Pantry> {
        return withContext(Dispatchers.IO) {
            mutableNetworkState.postValue(NetworkState.LOADING)
            try {
                val json = URL("https://raw.githubusercontent.com/end-hunger-durham/data-importer/master/pantries.json").readText()
                mutableNetworkState.postValue(NetworkState.SUCCESS)
                JSON.parse(PantryList.serializer(), json).pantries
            } catch (e: IOException) {
                //TODO: Retry (https://stackoverflow.com/a/47525583)
                mutableNetworkState.postValue(NetworkState.FAILURE)
                emptyList<Pantry>()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun filter(queryString: String) {
        query.value = queryString
    }

    fun reloadPantries() {
        uiScope.launch {
            pantriesRepo = fetchPantries()
        }
    }
}