package nz.scanner.app
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
data class UiState(
    val tab:Int=0, val region:String="All", val favourites:Set<String> = emptySet(),
    val current:String?=null, val playing:Boolean=false
)
class ScannerViewModel(app:Application):AndroidViewModel(app) {
    private val store=FavouritesStore(app)
    val state=combine(
        store.favourites, MutableStateFlow("All"), MutableStateFlow<Int>(0),
        MutableStateFlow<String?>(null), MutableStateFlow(false)
    ){ favs,region,tab,current,playing -> UiState(tab,region,favs,current,playing) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState())
    private val regionFlow = MutableStateFlow("All")
    private val tabFlow = MutableStateFlow(0)
    private val currentFlow = MutableStateFlow<String?>(null)
    private val playingFlow = MutableStateFlow(false)
    val uiState = combine(store.favourites,regionFlow,tabFlow,currentFlow,playingFlow) {
        favs,r,t,c,p -> UiState(t,r,favs,c,p)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState())
    fun setTab(v:Int){tabFlow.value=v}
    fun setRegion(v:String){regionFlow.value=v}
    fun favourite(id:String){viewModelScope.launch{store.toggle(id)}}
    fun current(id:String?){currentFlow.value=id}
    fun playing(v:Boolean){playingFlow.value=v}
}
