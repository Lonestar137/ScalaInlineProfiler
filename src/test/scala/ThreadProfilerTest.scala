package com.lonestar.inline

import org.scalatest.flatspec.AnyFlatSpec

class ThreadProfilerTest extends AnyFlatSpec {
  def test() {
    println("helloworld")
    def layerTwo() {
      Thread.sleep(1000)
      def layerThree() {
        Thread.sleep(1000)
      }
      layerThree()
    }
    layerTwo()
  }

  "ThreadProfiler" should "track main thread" in {
    val mainThreadName: String = Thread.currentThread().toString()

    val x = new ThreadProfiler(mainThreadName)
    x.startProfile()

    // For example: Your code block to profile here
    List(1, 2, 3).foreach { x =>
      Thread.sleep(1000)
      test()
    }

    println("Done profiling.")

    x.stopProfile()

  }

  // TODO: better / more tests.
}

