package com.example.filip.daggerarticle.dagger

import android.os.Process
import com.appunite.paints.dagger.ComputationScheduler
import com.appunite.paints.dagger.NetworkScheduler
import com.appunite.paints.dagger.UiScheduler
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Singleton

@Module
class SchedulersModule {

    companion object {
        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        private val CORE_POOL_SIZE = CPU_COUNT * 2 + 1
        private val MAXIMUM_POOL_SIZE = CPU_COUNT * 4 + 1
        private const val KEEP_ALIVE_SECONDS = 1
    }

    @Provides
    @UiScheduler
    fun provideUiScheduler(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    @Provides
    @Singleton
    @NetworkScheduler
    internal fun provideNetworkScheduler(): Scheduler {
        val workQueue = LinkedBlockingDeque<Runnable>(1000000)
        return Schedulers.from(ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_SECONDS.toLong(),
                TimeUnit.SECONDS,
                workQueue,
                NamedThreadFactory("network-paints", Process.THREAD_PRIORITY_DEFAULT)))
    }

    @Provides
    @Singleton
    @ComputationScheduler
    internal fun provideComputationScheduler(): Scheduler {
        val workQueue = LinkedBlockingDeque<Runnable>()
        return Schedulers.from(ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_SECONDS.toLong(),
                TimeUnit.SECONDS,
                workQueue,
                NamedThreadFactory("computation-paints", Process.THREAD_PRIORITY_DEFAULT)))
    }

    private class NamedThreadFactory constructor(private val name: String, private val threadPriority: Int) : ThreadFactory {
        private val threadNo = AtomicInteger(0)

        override fun newThread(runnable: Runnable): Thread {
            val threadName = name + ":" + threadNo.incrementAndGet()
            return Thread({
                android.os.Process.setThreadPriority(threadPriority)
                runnable.run()
            }, threadName)
        }
    }
}