package com.example.admin.rxandroidsample

import android.app.Activity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

// Sample From https://github.com/naokomada/RxAndroid/tree/2.x/sample-app
class MainActivity : Activity() {

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.button_run_scheduler).setOnClickListener(View.OnClickListener { onRunSchedulerExampleButtonClicked() })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    internal fun onRunSchedulerExampleButtonClicked() {
        disposables.add(sampleObservable()
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith<DisposableObserver<String>>(object : DisposableObserver<String>() {
                    override fun onComplete() {
                        Log.d(TAG, "onComplete()")
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError()", e)
                    }

                    override fun onNext(string: String) {
                        Log.d(TAG, "onNext($string)")
                    }
                }))
    }

    companion object {
        private val TAG = "RxAndroidSamples"

        internal fun sampleObservable(): Observable<String> {
            return Observable.defer {
                // Do some long running operation
                SystemClock.sleep(5000)
                Observable.just("one", "two", "three", "four", "five")
            }
        }
    }
}
