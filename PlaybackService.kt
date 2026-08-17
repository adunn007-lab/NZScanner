package nz.scanner.app
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
@UnstableApi
class PlaybackService:MediaSessionService(){
    private var session:MediaSession?=null
    override fun onCreate(){
        super.onCreate()
        val player=ExoPlayer.Builder(this).build()
        session=MediaSession.Builder(this,player).build()
    }
    override fun onGetSession(controllerInfo:MediaSession.ControllerInfo)=session
    override fun onDestroy(){
        session?.player?.release(); session?.release(); session=null; super.onDestroy()
    }
}
