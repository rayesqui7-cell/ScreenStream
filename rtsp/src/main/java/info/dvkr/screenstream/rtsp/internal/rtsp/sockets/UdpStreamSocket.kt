package info.dvkr.screenstream.rtsp.internal.rtsp.sockets

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.ConnectedDatagramSocket
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class UdpStreamSocket(
    private val selectorManager: SelectorManager,
    private val remoteHost: String,
    private val remotePort: Int,
    private val localPort: Int
) {
    private val ioMutex = Mutex()
    private val remoteSocketAddress = InetSocketAddress(remoteHost, remotePort)
    private var udpSocket: ConnectedDatagramSocket? = null

    suspend fun connect() = ioMutex.withLock {
        udpSocket = aSocket(selectorManager)
            .udp()
            .connect(remoteSocketAddress, InetSocketAddress("0.0.0.0", localPort))
    }

    suspend fun close() = ioMutex.withLock {
        runCatching { udpSocket?.close() }
        udpSocket = null
    }

    suspend fun write(bytes: ByteArray) = ioMutex.withLock {
        udpSocket?.send(Datagram(buildPacket { writeFully(bytes) }, remoteSocketAddress))
    }

    suspend fun write(bytes: ByteArray, offset: Int, length: Int) = ioMutex.withLock {
        udpSocket?.send(Datagram(buildPacket { writeFully(bytes, offset, length) }, remoteSocketAddress))
    }

    fun localPort(): Int? = (udpSocket?.localAddress as? InetSocketAddress)?.port
}