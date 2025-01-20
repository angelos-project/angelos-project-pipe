package org.angproj.io.ffi

import kotlinx.coroutines.yield
import org.angproj.io.Task


public class FileTask(task: suspend Task.() -> Unit) : Task(task) {

    override fun run() {
        suspend {
            while (true) {
                this as Task
                this.task()
                yield()
            }
        }
    }
}