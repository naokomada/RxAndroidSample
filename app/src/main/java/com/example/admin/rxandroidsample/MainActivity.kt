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

    // 複数のdisposableを管理する。.clearによって一度に購読を解除できる
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

    // ボタンクリックで処理開始
    internal fun onRunSchedulerExampleButtonClicked() {
        // disposable（購読解除用のインターフェース）を追加する。実際にはObservableを追加している。
        disposables.add(sampleObservable()
                // subscribeOnで指定されたスレッドでProducerの処理が行われる
                // Schedulers.ioは既定のブロッキングIO用のスレッド
                .subscribeOn(Schedulers.io())
                // データを受け取るConsumerが処理を行うスレッドを指定する
                // UIスレッドで受け取る
                .observeOn(AndroidSchedulers.mainThread())
                // Obserberの購読を行う
                .subscribeWith<DisposableObserver<String>>(object : DisposableObserver<String>() {
                    override fun onComplete() {
                        Log.d(TAG, "onComplete()")
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError()", e)
                    }

                    override fun onNext(string: String) {
                        Log.d("mydebug", "onNext Thread : " + Thread.currentThread().name)
                        Log.d(TAG, "onNext($string)")
                    }
                }))
    }

    companion object {
        private val TAG = "RxAndroidSamples"

        internal fun sampleObservable(): Observable<String> {
            // deferは購読されるたびに新しいObservableを生成する
            return Observable.defer {
                Log.d("mydebug", "sampleObservable Thread : " + Thread.currentThread().name)

                // Do some long running operation
                SystemClock.sleep(5000)

                // justで値を受け取る
                Observable.just("one", "two", "three", "four", "five")
            }
        }
    }
}
