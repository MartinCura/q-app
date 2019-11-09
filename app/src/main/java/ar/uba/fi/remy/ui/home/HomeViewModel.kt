package ar.uba.fi.remy.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "¿Cocinamos algo?"
    }
    val text: LiveData<String> = _text
}
