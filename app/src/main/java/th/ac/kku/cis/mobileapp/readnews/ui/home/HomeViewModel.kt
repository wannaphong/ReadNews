package th.ac.kku.cis.mobileapp.readnews.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "You haven't added any news sources yet."
    }
    val text: LiveData<String> = _text
}