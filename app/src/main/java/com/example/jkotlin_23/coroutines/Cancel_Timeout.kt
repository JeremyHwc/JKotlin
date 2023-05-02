package com.example.jkotlin_23.coroutines

import kotlinx.coroutines.*

/**
 * 文件说明：
 * 该文件的的所有案例来源：https://www.kotlincn.net/docs/reference/coroutines/cancellation-and-timeouts.html
 */

/**
 * 例1：取消协程的执行
 */
//fun main() = runBlocking {
//    val job = launch {
//        repeat(1000) { i ->
//            println("job: I'm sleeping $i ...")
//            delay(500L)
//        }
//    }
//    delay(1300L) // 延迟一段时间
//    println("main: I'm tired of waiting!")
//    job.cancel() // 取消该作业
//    job.join() // 等待作业执行结束
//    // Cancels the job and suspends the invoking coroutine until the cancelled job is complete.
////    job.cancelAndJoin()
//    println("main: Now I can quit.")
//}

/**
 * 例2：取消是协作的。
 * 如下代码在调用了Job#cancelAndJoin方法之后，依然会有job中的输出继续打印
 *  job: I'm sleeping 0 ...
    job: I'm sleeping 1 ...
    job: I'm sleeping 2 ...
    main: I'm tired of waiting!
    job: I'm sleeping 3 ...
    job: I'm sleeping 4 ...
    main: Now I can quit.
 */
//fun main() = runBlocking {
//    val startTime = System.currentTimeMillis()
//    val job = launch(Dispatchers.Default) {
//        var nextPrintTime = startTime
//        var i = 0
//        while (i < 5) { // 一个执行计算的循环，只是为了占用 CPU
//            // 每秒打印消息两次
//            if (System.currentTimeMillis() >= nextPrintTime) {
//                println("job: I'm sleeping ${i++} ...")
//                nextPrintTime += 500L
//            }
//        }
//    }
//    delay(1300L) // 等待一段时间
//    println("main: I'm tired of waiting!")
//    job.cancelAndJoin() // 取消一个作业并且等待它结束
//    println("main: Now I can quit.")
//}

/**
 * 例3：使计算代码可取消
 * 可以看到，现在循环被取消了。isActive 是一个可以被使用在 CoroutineScope 中的扩展属性。
 *
 * 输出：
 *  job: I'm sleeping 0 ...
    job: I'm sleeping 1 ...
    job: I'm sleeping 2 ...
    main: I'm tired of waiting!
    main: Now I can quit.
 */
//fun main() = runBlocking {
//    val startTime = System.currentTimeMillis()
//    val job = launch(Dispatchers.Default) {
//        var nextPrintTime = startTime
//        var i = 0
//        while (isActive) { // 可以被取消的计算循环
//            // 每秒打印消息两次
//            if (System.currentTimeMillis() >= nextPrintTime) {
//                println("job: I'm sleeping ${i++} ...")
//                nextPrintTime += 500L
//            }
//        }
//    }
//    delay(1300L) // 等待一段时间
//    println("main: I'm tired of waiting!")
//    job.cancelAndJoin() // 取消该作业并等待它结束
//    println("main: Now I can quit.")
//}
/**
 * 例4：在finally中释放资源
 * 我们通常使用如下的方法处理在被取消时抛出 CancellationException 的可被取消的挂起函数。
 * 比如说，try {……} finally {……} 表达式以及 Kotlin 的 use 函数一般在协程被取消的时候执行它们的终结动作：
 */
//fun main() = runBlocking {
//    val job = launch {
//        try {
//            repeat(1000) {
//                println("job: I'm sleeping $it")
//                delay(500L)
//            }
//        } finally {
//            println("job: I'm running finally")
//        }
//    }
//    delay(1300L)
//    println("main: I'm tired of waiting!")
//    job.cancelAndJoin() // 取消该作业并且等待它结束
//    println("main: Now I can quit.")
//}
/**
 * 例5：运行不能取消的代码块
 * 在前一个例子中任何尝试在 finally 块中调用挂起函数的行为都会抛出 CancellationException，因为这里持续运行的代码是
 * 可以被取消的。通常，这并不是一个问题，所有良好的关闭操作（关闭一个文件、取消一个作业、或是关闭任何一种通信通道）通常都
 * 是非阻塞的，并且不会调用任何挂起函数。然而，在真实的案例中，当你需要挂起一个被取消的协程，你可以将相应的代码包装在
 * withContext(NonCancellable) {……} 中，并使用 withContext 函数以及 NonCancellable 上下文，见如下示例所示：
 */
//fun main() = runBlocking {
//    val job = launch {
//        try {
//            repeat(1000) { i ->
//                println("job: I'm sleeping $i ...")
//                delay(500L)
//            }
//        } finally {
//            withContext(NonCancellable) {
//                println("job: I'm running finally")
//                delay(1000L)
//                println("job: And I've just delayed for 1 sec because I'm non-cancellable")
//            }
//        }
//    }
//    delay(1300L) // 延迟一段时间
//    println("main: I'm tired of waiting!")
//    job.cancelAndJoin() // 取消该作业并等待它结束
//    println("main: Now I can quit.")
//}
/**
 * 例6_1：超时1
 * withTimeout 抛出了 TimeoutCancellationException，它是 CancellationException 的子类。 我们之前没有在控制台
 * 上看到堆栈跟踪信息的打印。这是因为在被取消的协程中 CancellationException 被认为是协程执行结束的正常原因。
 * 然而，在这个示例中我们在 main 函数中正确地使用了 withTimeout。
 */
//fun main() = runBlocking {
//    withTimeout(1200L) {
//        repeat(1000) {
//            println("I'm sleeping $it")
//            delay(500L)
//        }
//    }
//}

/**
 * 例6_2：超时2
 * 由于取消只是一个例外，所有的资源都使用常用的方法来关闭。 如果你需要做一些各类使用超时的特别的额外操作，可以使用类似
 * withTimeout 的 withTimeoutOrNull 函数，并把这些会超时的代码包装在 try {...} catch (e: TimeoutCancellationException) {...}
 * 代码块中，而 withTimeoutOrNull 通过返回 null 来进行超时操作，从而替代抛出一个异常：
 */

//fun main() = runBlocking {
//    val result = withTimeoutOrNull(1300L) {
//        repeat(1000) {
//            println("I'm sleeping $it")
//            delay(500L)
//        }
//        "Done"
//    }
//
//    println("Result is $result")
//}


/**
 * 例7：Asynchronous timeout and resources
 */
// TODO: Not understand.
fun main() = runBlocking {
    var acquired = 0

    class Resource {
        init { acquired++ } // Acquire the resource
        fun close() { acquired-- } // Release the resource
    }

    fun main() {
        runBlocking {
            repeat(100_000) { // Launch 100K coroutines
                launch {
                    val resource = withTimeout(60) { // Timeout of 60 ms
                        delay(50) // Delay for 50 ms
                        Resource() // Acquire a resource and return it from withTimeout block
                    }
                    resource.close() // Release the resource
                }
            }
        }
        // Outside of runBlocking all coroutines have completed
        println(acquired) // Print the number of resources still acquired
    }
}