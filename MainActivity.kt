package nz.scanner.app

import android.Manifest
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors

class MainActivity:ComponentActivity(){
    override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{NZScannerApp()}}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun NZScannerApp(vm:ScannerViewModel=viewModel()){
    val state by vm.uiState.collectAsState()
    val context=androidx.compose.ui.platform.LocalContext.current
    var controller by remember{mutableStateOf<MediaController?>(null)}
    val notification=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()){}
    LaunchedEffect(Unit){
        val token=SessionToken(context,ComponentName(context,PlaybackService::class.java))
        val future=MediaController.Builder(context,token).buildAsync()
        future.addListener({controller=future.get()},MoreExecutors.directExecutor())
    }
    MaterialTheme{
        Scaffold(
            topBar={TopAppBar(title={Text("NZ Scanner")},actions={
                IconButton(onClick={
                    if(Build.VERSION.SDK_INT>=33) notification.launch(Manifest.permission.POST_NOTIFICATIONS)
                }){Icon(Icons.Default.Notifications,"Notifications")}
            })},
            bottomBar={
                NavigationBar{
                    listOf(Icons.Default.Headphones to "Live",Icons.Default.Favorite to "Favourites",
                        Icons.Default.LocationOn to "Regions",Icons.Default.Settings to "Settings").forEachIndexed{i,p->
                        NavigationBarItem(selected=state.tab==i,onClick={vm.setTab(i)},icon={Icon(p.first,p.second)},label={Text(p.second)})
                    }
                }
            }
        ){padding->
            Box(Modifier.fillMaxSize().padding(padding)){when(state.tab){
                0->LiveScreen(state,vm,controller)
                1->FavouritesScreen(state,vm,controller)
                2->RegionsScreen(state,vm)
                else->SettingsScreen()
            }}
        }
    }
}

@Composable fun LiveScreen(state:UiState,vm:ScannerViewModel,controller:MediaController?){
    val regions=listOf("All")+StreamRepository.streams.map{it.region}.distinct()
    val streams=StreamRepository.streams.filter{state.region=="All"||it.region==state.region}
    Column(Modifier.fillMaxSize().padding(16.dp)){
        Text("Live scanner",style=MaterialTheme.typography.headlineSmall)
        Text("Authorized public streams only.",style=MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(10.dp))
        LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){
            items(regions){r->FilterChip(selected=state.region==r,onClick={vm.setRegion(r)},label={Text(r)})}
        }
        Spacer(Modifier.height(10.dp))
        LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp)){
            items(streams,key={it.id}){StreamCard(it,it.id in state.favourites,state.current==it.id&&state.playing,vm,controller)}
        }
    }
}
@Composable fun FavouritesScreen(state:UiState,vm:ScannerViewModel,controller:MediaController?){
    val streams=StreamRepository.streams.filter{it.id in state.favourites}
    Column(Modifier.fillMaxSize().padding(16.dp)){Text("Favourites",style=MaterialTheme.typography.headlineSmall);Spacer(Modifier.height(12.dp))
        if(streams.isEmpty())Text("No favourites yet.")
        LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp)){items(streams){StreamCard(it,true,state.current==it.id&&state.playing,vm,controller)}}
    }
}
@Composable fun RegionsScreen(state:UiState,vm:ScannerViewModel){
    Column(Modifier.fillMaxSize().padding(16.dp)){Text("Regions",style=MaterialTheme.typography.headlineSmall);Spacer(Modifier.height(12.dp))
        StreamRepository.streams.map{it.region}.distinct().forEach{r->
            ListItem(headlineContent={Text(r)},supportingContent={Text("${StreamRepository.streams.count{it.region==r}} configured stream(s)")},
                trailingContent={Button(onClick={vm.setRegion(r);vm.setTab(0)}){Text("View")}})
        }
    }
}
@Composable fun SettingsScreen(){
    Column(Modifier.fillMaxSize().padding(16.dp)){Text("Settings",style=MaterialTheme.typography.headlineSmall);Spacer(Modifier.height(12.dp))
        ListItem(headlineContent={Text("Notifications")},supportingContent={Text("Use the bell in the top bar to grant notification permission.")})
        ListItem(headlineContent={Text("Stream sources")},supportingContent={Text("Only add feeds you are authorized to redistribute.")})
        ListItem(headlineContent={Text("Privacy")},supportingContent={Text("This starter does not require an account or collect location.")})
    }
}
@Composable fun StreamCard(s:ScannerStream,favourite:Boolean,playing:Boolean,vm:ScannerViewModel,controller:MediaController?){
    Card(Modifier.fillMaxWidth()){Column(Modifier.padding(16.dp)){
        Row(verticalAlignment=Alignment.CenterVertically){
            Column(Modifier.weight(1f)){Text(s.name,style=MaterialTheme.typography.titleMedium);Text("${s.region} • ${s.service}")}
            IconButton(onClick={vm::favourite.bind(s.id)}){Icon(if(favourite)Icons.Default.Favorite else Icons.Default.FavoriteBorder,"Favourite")}
        }
        Text(s.description,style=MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(8.dp))
        Button(Modifier.fillMaxWidth(),onClick={
            if(playing){controller?.stop();vm.playing(false)}
            else {controller?.setMediaItem(MediaItem.fromUri(s.streamUrl));controller?.prepare();controller?.play();vm.current(s.id);vm.playing(true)}
        }){Icon(if(playing)Icons.Default.Stop else Icons.Default.PlayArrow,null);Spacer(Modifier.width(6.dp));Text(if(playing)"Stop" else "Play")}
    }}
}
private fun <T,R> (T.()->Unit).bind(value:R):()->Unit = { this() }
