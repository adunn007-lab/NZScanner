package nz.scanner.app
data class ScannerStream(
    val id:String, val name:String, val region:String, val service:String,
    val streamUrl:String, val description:String, val authorized:Boolean=true
)
data class AlertItem(val id:String, val title:String, val body:String, val region:String)
