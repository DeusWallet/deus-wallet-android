package io.deus.wallet.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.deus.wallet.core.IEvmNodeSyncStorage
import io.deus.wallet.core.storage.EvmNodeSyncRecord
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.security.SecureRandom
import java.util.UUID


@Keep
data class Node(
    val url: String,
    val height: Int,
    val latency: Int
)

@Keep
data class ChainNodes (
    val chainId: Int,
    val nodes: List<Node>
)

 data class BatchBlock(
    val data: ByteArray
)

object EvmNodeSync {
    const val BLOCK_MAX_SIZE = 32

    var androidId: String = ""
    var evmNodeSyncStorageEv: IEvmNodeSyncStorage? = null

    val client = OkHttpClient()

    private val gson: Gson by lazy {
        GsonBuilder()
            .disableHtmlEscaping()
            .enableComplexMapKeySerialization()
            .create()
    }

    fun setEvmNodeSyncStorage(storage: IEvmNodeSyncStorage) {
        evmNodeSyncStorageEv = storage
    }

    @SuppressLint("HardwareIds")
    fun setDeviceId(context: Context): String {
        val androidId =  Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        this.androidId = androidId
        return androidId
    }

    fun getDeviceId(): String {
        val uuid = UUID.nameUUIDFromBytes(androidId.toByteArray())
        return uuid.toString()
    }

    private fun getHash(): String {
        return generateRandom(39).toHexString()
    }

    private fun generateRandom(size: Int): ByteArray {
        val random = SecureRandom()
        val value = ByteArray(size)
        random.nextBytes(value)
        return value
    }

    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02x".format(it) }
    }

    fun getRpcNodes() {
        val nowTimestamp = System.currentTimeMillis()
        val hash = getHash()
        val url = "https://api.deuswallet.com/chains/1/rpc?timestamp=$nowTimestamp"
        val deviceId = getDeviceId()

        val request = Request.Builder()
            .url(url)
            .header("if-none-match", hash)
            .header("x-client-platform", "android")
            .header("x-client-uuid", deviceId)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("EvmNodeSync", "Request failed")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.code != 200) {
                    Log.d("EvmNodeSync", "Response code is not 200")
                } else {
                    val nodes = gson.fromJson(response.body.string(), ChainNodes::class.java)
                    evmNodeSyncStorageEv?.clear()
                    for (node in nodes.nodes) {
                        evmNodeSyncStorageEv?.save(EvmNodeSyncRecord(nodes.chainId, node.url, node.height, node.latency))
                    }
                }
            }
        })
    }
}