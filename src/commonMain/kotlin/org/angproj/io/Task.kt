package org.angproj.io

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.yield

@OptIn(DelicateCoroutinesApi::class)
public abstract class Task(protected val task: suspend Task.() -> Unit) : Runnable {

    init {
        Dispatchers.Default.dispatch(GlobalScope.coroutineContext, this)
    }

    override fun run() {
        suspend {
            val lim = Dispatchers.Default.limitedParallelism(1)
            while (true) {
                task()
                yield()
            }
        }
    }
}

public fun task(block: suspend Task.() -> Unit): Unit = object : Task(block) {}.run()