package org.endhungerdurham.pantries.ui.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.serialization.json.JSON
import org.endhungerdurham.pantries.Pantry
import org.endhungerdurham.pantries.PantryList
import java.io.IOException
import java.net.URL

class PantriesViewModel: ViewModel() {
    private var pantriesRepo: List<Pantry> = emptyList()
    private val mutablePantries: MutableLiveData<List<Pantry>> = MutableLiveData()
    val pantries: LiveData<List<Pantry>> = mutablePantries

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        reloadPantries()
    }

    private suspend fun fetchPantries(): List<Pantry>? {
        return withContext(Dispatchers.IO) {
            try {
                val json = URL("https://raw.githubusercontent.com/end-hunger-durham/data-importer/master/pantries.json").readText()
                JSON.parse(PantryList.serializer(), json).pantries
            } catch (e: IOException) {
                //TODO: Retry (https://stackoverflow.com/a/47525583)
                null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun filterPantries(filter: String) {
        if (filter.equals("")) {
            mutablePantries.postValue(pantriesRepo)
        } else {
            mutablePantries.postValue(pantriesRepo.filter { item ->
                with(item) {
                    false.takeUnless { organizations.contains(filter, ignoreCase = true) }
                            ?.takeUnless { address.contains(filter, ignoreCase = true) }
                            ?.takeUnless { city.contains(filter, ignoreCase = true) } ?: true
                }
            })
        }
    }

    fun reloadPantries() {
        uiScope.launch {
            pantriesRepo = fetchPantries() ?: emptyList()
            // TODO: use smarter filtering so that filter is preserved across reload
            mutablePantries.postValue(pantriesRepo)
        }
    }
}