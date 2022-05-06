package pt.isel.pdm.chess4android.utils

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.Executors

private val ioExecutor = Executors.newSingleThreadExecutor()

private fun <T> executeAndCollectResult(asyncAction: () -> T): Result<T> =
    try { Result.success(asyncAction()) }
    catch (e: Exception) { Result.failure(e) }



fun <T> callbackAfterAsync(callback: (Result<T>) -> Unit, asyncAction: () -> T) {
    val mainHandler = Handler(Looper.getMainLooper())
    ioExecutor.submit {
        val result = executeAndCollectResult(asyncAction)
        mainHandler.post {
            callback(result)
        }
    }
}


fun <T> publishInLiveDataAfterAsync(asyncAction: () -> T): LiveData<Result<T>> {
    val toPublish = MutableLiveData<Result<T>>()
    ioExecutor.submit {
        val result = executeAndCollectResult(asyncAction)
        toPublish.postValue(result)
    }
    return toPublish
}