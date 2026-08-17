package nz.scanner.app
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
private val Context.dataStore by preferencesDataStore("scanner_preferences")
class FavouritesStore(private val context:Context) {
    private val key=stringSetPreferencesKey("favourites")
    val favourites:Flow<Set<String>> = context.dataStore.data.map { it[key] ?: emptySet() }
    suspend fun toggle(id:String) = context.dataStore.edit {
        val set=(it[key] ?: emptySet()).toMutableSet()
        if(!set.add(id)) set.remove(id)
        it[key]=set
    }
}
