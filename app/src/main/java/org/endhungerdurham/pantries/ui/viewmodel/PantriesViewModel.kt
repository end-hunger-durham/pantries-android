package org.endhungerdurham.pantries.ui.viewmodel

import android.app.Application
import android.arch.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.JSON
import org.endhungerdurham.pantries.Pantry
import org.endhungerdurham.pantries.PantryList
import org.endhungerdurham.pantries.R
import java.io.IOException
import java.net.URL

enum class NetworkState {
    SUCCESS,
    LOADING,
    FAILURE,
}

class DoubleTrigger<A, B>(a: LiveData<A>, b: LiveData<B>) : MediatorLiveData<Pair<A?, B?>>() {
    init {
        addSource(a) { value = it to b.value }
        addSource(b) { value = a.value to it }
    }
}

class PantriesViewModel(application: Application): AndroidViewModel(application) {
    private val urlString: String ?= application.resources.getString(R.string.pantries_json_url)
    private var pantriesRepo: MutableLiveData<List<Pantry>> = MutableLiveData()
    private val query = MutableLiveData<String>()
    private val mutableNetworkState: MutableLiveData<NetworkState> = MutableLiveData()
    val networkState: LiveData<NetworkState> = mutableNetworkState
    val pantries: LiveData<List<Pantry>> = Transformations.switchMap(DoubleTrigger(pantriesRepo, query)) {
        val repo = it?.first ?: emptyList()
        val query = it?.second ?: ""

        val filtered = MutableLiveData<List<Pantry>>()
        filtered.value = when (query) {
            "" -> repo
            else -> {
                repo.filter { pantry ->
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
                val json = URL(urlString).readText()
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
        query.postValue(queryString)
    }

    fun reloadPantries() {
        uiScope.launch {
            pantriesRepo.postValue(fetchPantries())
        }
    }
}