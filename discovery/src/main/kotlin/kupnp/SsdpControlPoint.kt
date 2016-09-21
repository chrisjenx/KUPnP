package kupnp

import okio.ByteString
import rx.AsyncEmitter
import rx.Observable
import rx.Observable.*
import rx.exceptions.Exceptions
import rx.observables.ConnectableObservable
import rx.schedulers.Schedulers
import java.net.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by chris on 16/04/2016.
 * For project kupnp
 */
class SsdpControlPoint(private val ssdpMessage: SsdpMessage) {

    private val request: ByteString

    init {
        request = ssdpMessage.byteString()
    }

    val broadcastAddress: InetSocketAddress
        get() = InetSocketAddress(InetAddress.getByName(SSDP_IP), SSDP_PORT)
    val multicastPacket: DatagramPacket by lazy { buildMulticastPacket() }

    /**
     * This will start broadcasting out and listening for `ssdpMessage.mx` seconds past the last broadcast.
     * (The broadcast sends three 0.2 - mx random interval broadcasts to make sure we alleviate for any weird packet loss.
     */
    fun start(): Observable<ByteString> {
        return Observable
                .using({
                    createSockets()
                }, { sockets ->
                    val sender = createSender(sockets.map { it.socket })
                    val receivers = sockets.map { createReceiver(it.socket) }
                    bind(sockets)
                            .flatMap { merge(receivers).mergeWith(sender.doOnSubscribe { sender.connect() }) }
                            .takeUntil(sender.defaultIfEmpty(null).delay(ssdpMessage.mx.toLong(), TimeUnit.SECONDS))
                }, {
                    it.forEach { it.socket.closeQuietly() }
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

        // Create a new Socket for each Address, most devices are multi-honed these days.
        return localAddresss.map { AddressAndSocket(it.address, MulticastSocket(null)) }
    }

    internal fun bind(sockets: List<AddressAndSocket>): Observable<Unit> {
        return Observable.fromCallable {
            sockets.forEach {
                log("Creating bound socket (for datagram input/output) on: ${it.address}")
                it.socket.bind(InetSocketAddress(it.address, 0))
            }
            return@fromCallable null
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
                            it.onNext(ByteString.of(receivePacket.data, receivePacket.offset, receivePacket.length))
                            // Reset packet size
                            receivePacket.length = receiveData.size
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

    /**
     * Create a Observable that will send the SSDP Message over the sockets we have bound. This will fire the broadcast
     * 3 times at random intervals no more than 1/2 the timeout value of the SSDP message.
     *
     * This doesn't emmit anything it ignores the outputs then just completes
     */
    internal fun createSender(sockets: List<DatagramSocket>): ConnectableObservable<ByteString> {
        val sendMessage = Observable
                .fromCallable {
                    sockets.forEach {
                        try {
                            it.send(multicastPacket)
                            log("Sent multicast packet from ${it.localAddress}")
                        } catch (ex: SocketException) {
                            warn("Socket closed, aborting datagram send to: ${multicastPacket.address}")
                        } catch (ex: RuntimeException) {
                            warn("Runtime Exception sending datagram: ${ex.message}")
                            throw ex
                        } catch (ex: Exception) {
                            warn("Exception sending datagram to: ${multicastPacket.address}: ${ex.message}")
                        }
                    }
                    return@fromCallable null
                }

        return concat(
                fromEmitter<Observable<Long>>({ e ->
                    val r = Random()
                    // Fire first message straight away
                    e.onNext(Observable.just(0L))
                    for (i in 0..1) {
                        val rand = 200 + r.nextInt(ssdpMessage.mx * 1000)
                        e.onNext(Observable.timer(rand.toLong(), TimeUnit.MILLISECONDS))
                    }
                    e.onCompleted()
                }, AsyncEmitter.BackpressureMode.BUFFER))
                .flatMap { sendMessage }
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

    internal data class AddressAndSocket(val address: InetAddress, val socket: DatagramSocket)

    companion object {
        internal const val SSDP_PORT = 1900
        internal const val SSDP_IP = "239.255.255.250"
    }

}