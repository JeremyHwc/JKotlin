package com.example.jkotlin_23.coroutines


import kotlinx.coroutines.*

/**
 * 文件说明：
 * 该文件中的所有实例来源于：https://www.kotlincn.net/docs/reference/coroutines/basics.html
 */



// 例1：协程Hello World！
//fun main(args: Array<String>) {
//    GlobalScope.launch {// 在后台启动一个新的协程并继续
//        delay(1000L)// 非阻塞的等待1秒钟（默认时间单位ms）
//        println("World!") // 在延迟后打印输出
//    }
//    println("Hello,") // 协程已在等待时主线程还在继续
//    Thread.sleep(2000L) // 阻塞主线程2秒来保证JVM存活
//}

// 例2：桥接阻塞与非阻塞的世界
//fun main(args: Array<String>) {
//    GlobalScope.launch {
//        delay(1000L)
//        println("World!")
//    }
//    println("Hello,")
//    runBlocking {// 阻塞主线程。调用了runBlocking的主线程会一直阻塞知道runBlocking内部的协程执行完毕。
//        delay(2000L) // 延迟2秒来保证JVM的存活
//    }
//}

// 例3：针对例2进行惯用法的方式重写
//fun main(args: Array<String>) = runBlocking<Unit> {
//    GlobalScope.launch {
//        delay(1000L)
//        println("World!")
//    }
//    println("Hello,")
//    delay(2000L)
//}

// 例4：等待一个作业。延迟一段时间等待另一个协程运行并不是一个好的选择，通过job.join这种非阻塞方式等待所启动的后台Job执行结束
//fun main() = runBlocking {
//    val job = GlobalScope.launch {
//        delay(1000L)
//        println("World!")
//    }
//    println("Hello,")
//    job.join()
//}

// 例5：结构化的并发
// 问题：如果对于一个协程挂起的时间太长，那么该协程就会一直占有资源，导致资源浪费
// 解决方式：在执行操作所在的指定作用域内启动协程，而不是像通常使用线程（线程总是全局的）那样在GlobalScope中启动
// 外部协程（示例中的runBlocking）直到在其作用域中启动的所有协程都执行完毕后才会结束
//fun main() = runBlocking { // this: CoroutineScope
//    launch { // 在 runBlocking 作用域中启动一个新协程
//        delay(1000L)
//        println("World!")
//    }
//    println("Hello,")
//}

// 例6：作用域构建器
// 使用coroutineScope构建器声明自己的作用域。她会创建一个协程作用域并且在所有已启动子协程执行完毕之前不会结束。
// runBlocking与coroutineScope看起来很类似。因为它们都会等待其协程体以及所有子协程结束。
// 区别：runBlocking会阻塞当前线程来等待，而coroutineScope只是挂起，会释放底层线程用于其他用途。
// 所以runBlocking是常规函数，而coroutineScope是挂起函数
//fun main() = runBlocking {
//    launch {
//        delay(200L)
//        println("Task from runBlocking")
//    }
//
//    coroutineScope {
//        launch {
//            delay(500L)
//            println("Task from nested launch")
//        }
//        delay(100L)
//        println("Task from coroutine scope")
//    }
//
//    println("Coroutine scope is over")
//}

// 例7：提取函数重构
// 将协程内部的代码抽取为一个方法时，默认该方法会带有一个suspend修饰符。
// 问题：但是如果提取出的函数包含一个在当前作用域中调用的协程构建器的话，该怎么办？
// 解决方式：所提取的函数上只有suspend修饰符是不够的。惯用的解决方案是要么显式将
// CoroutineScope 作为包含该函数的类的一个字段， 要么当外部类实现了 CoroutineScope 时隐式取得。 作为最后的手段，可以使用 CoroutineScope(coroutineContext)，不过这种方法结构上不安全， 因为你不能再控制该方法执行的作用域。只有私有 API 才能使用这个构建器。
// TODO: 解决方式没搞懂
//fun main() = runBlocking {
//    launch {
//        doWorld()
//    }
//    println("Hello,")
//}
//
//private suspend fun doWorld() {
//    delay(1000L)
//    println("World!")
//}

// 例8：协程很轻量
//fun main() = runBlocking {
//    repeat(100_000) {// 启动大量的协程
//        launch {
//            delay(5000L)
//            println("${it}.")
//        }
//    }
//}

// 例9：全局协程像守护线程
// 在 GlobalScope 中启动的活动协程并不会使进程保活。它们就像守护线程。
fun main() = runBlocking {
    GlobalScope.launch {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
    }
    delay(1300L) // 在延迟后退出
}


