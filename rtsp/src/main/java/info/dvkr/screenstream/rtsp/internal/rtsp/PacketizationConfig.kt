package info.dvkr.screenstream.rtsp.internal.rtsp

internal object PacketizationConfig {
    @Volatile var paramReinjectionEnabled: Boolean = true
    @Volatile var reinjectParamsIntervalSec: Int = 2
    @Volatile var requireFirstIdrForHevc: Boolean = true
    @Volatile var requireFirstIdrForAvc: Boolean = false
}

