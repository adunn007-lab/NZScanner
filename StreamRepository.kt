package nz.scanner.app
object StreamRepository {
    // Replace these development URLs only with streams you are authorized to redistribute.
    val streams = listOf(
        ScannerStream("demo-auckland","Auckland Public Safety Demo","Auckland","Public stream",
            "https://example.com/replace-with-authorized-stream.mp3",
            "Development placeholder — no operational Police feed."),
        ScannerStream("demo-wellington","Wellington Public Safety Demo","Wellington","Public stream",
            "https://example.com/replace-with-authorized-stream.mp3",
            "Development placeholder — no operational Police feed."),
        ScannerStream("demo-canterbury","Canterbury Public Safety Demo","Canterbury","Public stream",
            "https://example.com/replace-with-authorized-stream.mp3",
            "Development placeholder — no operational Police feed."),
        ScannerStream("demo-waikato","Waikato Public Safety Demo","Waikato","Public stream",
            "https://example.com/replace-with-authorized-stream.mp3",
            "Development placeholder — no operational Police feed.")
    )
    val alerts = listOf(
        AlertItem("sample-1","Feed alerts are ready","Configure an authorized public stream to receive playback alerts.","Auckland")
    )
}
