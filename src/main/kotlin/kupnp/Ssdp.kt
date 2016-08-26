package kupnp

import okio.ByteString
import rx.Observable
import rx.exceptions.Exceptions
import rx.observables.ConnectableObservable
import rx.schedulers.Schedulers
import java.net.*

/**
 * Created by chris on 16/04/2016.
 * For project kupnp
 */
class Ssdp(ssdpMessage: SsdpMessage) {

    private val request: ByteString

    init {
        request = ssdpMessage.byteString()
    }

    val broadcastAddress: InetSocketAddress
        get() = InetSocketAddress(InetAddress.getByName(SSDP_IP), SSDP_PORT)
    val multicastPacket: DatagramPacket by lazy { buildMulticastPacket() }

    fun start(): Observable<ByteString> {
        return Observable
                .using({
                    createSockets()
                }, { sockets ->
                    val sender = createSender(sockets.map { it.datagramSocket })
                    val receivers = sockets.map { createReceiver(it.datagramSocket) }
                    bind(sockets).flatMap { Observable.merge(receivers).mergeWith(sender.doOnSubscribe { sender.connect() }) }
                }, {
                    it.forEach { it.datagramSocket.closeQuietly() }
                })
    }

    internal fun buildMulticastPacket(): DatagramPacket {
        val sendData = request.toByteArray()
        /* create a packet from our data destined for 239.255.255.250:1900 */
        return DatagramPacket(sendData, sendData.size, broadcastAddress.address, SSDP_PORT)
    }

    /**
     * Creates an unbound sockets to local interfaces
     */
    internal fun createSockets(): List<AddressAndSocket> {
        data class AddressAndInterface(val address: InetAddress, val networkInterface: NetworkInterface)

        val localAddresss = NetworkInterface.getNetworkInterfaces().toList()
                .filter { it.isUp && !it.isLoopback }
                .map { inter ->
                    inter.inetAddresses.toList().map { AddressAndInterface(it, inter) }
                }
                .flatMap { it }
                .filter { it.address is Inet4Address }
                .filter { it.address.isSiteLocalAddress }

        return localAddresss.map {
            AddressAndSocket(it.address, MulticastSocket(null))
        }
    }

    internal fun bind(sockets: List<AddressAndSocket>): Observable<Unit> {
        return Observable.fromCallable {
            sockets.forEach {
                log("Creating bound socket (for datagram input/output) on: ${it.address}")
                it.datagramSocket.bind(InetSocketAddress(it.address, 0))
            }
        }
    }

    /**
     * Subscribes on a different thread and will keep listening until you unsubscribe
     */
    internal fun createReceiver(socket: DatagramSocket): Observable<ByteString> {
        val receiveData = ByteArray(1024)
        return Observable
                .create<ByteString> {
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    while (!it.isUnsubscribed) {
                        try {
                            socket.receive(receivePacket)
                            it.onNext(ByteString.of(*receivePacket.data))
                        } catch (se: SocketException) {
                            it.onCompleted()
                            break
                        } catch(se: SocketTimeoutException) {
                            it.onCompleted()
                            break
                        } catch (e: Exception) {
                            Exceptions.throwOrReport(e, it)
                        }
                    }
                    socket.closeQuietly()
                }
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
    }

    internal fun createSender(sockets: List<DatagramSocket>): ConnectableObservable<ByteString> {
        return Observable
                .fromCallable {
                    sockets.forEach {
                        try {
                            it.send(multicastPacket)
                            log("Sent multicast packet from ${it.localAddress}")
                        } catch (ex: SocketException) {
                            log("Socket closed, aborting datagram send to: ${multicastPacket.address}")
                        } catch (ex: RuntimeException) {
                            throw ex
                        } catch (ex: Exception) {
                            log("Exception sending datagram to: ${multicastPacket.address}: ${ex.message}")
                        }
                    }
                    return@fromCallable null
                }
                .subscribeOn(Schedulers.io())
                .ignoreElements()
                .cast(ByteString::class.java)
                .publish()
    }

    fun DatagramSocket.closeQuietly() {
        if (this.isClosed) return
        try {
            this.close()
        } catch (ignore: Exception) {
        }
    }

    internal data class AddressAndSocket(val address: InetAddress, val datagramSocket: DatagramSocket)

    companion object {
        internal const val SSDP_PORT = 1900
        internal const val SSDP_IP = "239.255.255.250"
    }

}