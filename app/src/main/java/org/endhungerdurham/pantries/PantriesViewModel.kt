package org.endhungerdurham.pantries

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.serialization.json.JSON
import java.io.IOException
import java.net.URL

class PantriesViewModel: ViewModel() {
    private lateinit var pantries: MutableLiveData<List<Pantry>>
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

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

    fun getPantries(): LiveData<List<Pantry>> {
        if (!::pantries.isInitialized) {
            pantries = MutableLiveData()
            uiScope.launch {
                pantries.value = fetchPantries()
            }
        }
        return pantries
    }
}