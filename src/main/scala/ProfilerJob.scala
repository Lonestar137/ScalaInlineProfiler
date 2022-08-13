package com.lonestar.inline.job

import com.lonestar.inline.ThreadProfiler
import java.io.FileWriter
import scala.collection.JavaConversions.mapAsScalaMap

// TODO: add option to specify file path w/ support for Windows
// TODO: add option to specify stackCheckIntervalInMilliseconds, make sure to prevent negative numbers w/ .abs
class ProfilerJob(threadToWatch: String = "Thread[main,5,main]")
    extends Runnable {

  val stackCheckIntervalInMilliseconds = 5
  var stackMap = Map[String, List[StackTraceElement]]()
  val dataFile: FileWriter = new FileWriter(
    "src/main/resources/InlineScalaProfiler.data",
    true
  )

  def getStackInfo(threadStack: StackTraceElement): String = {
    val className: String = threadStack.getClassName()
    val methodName: String = threadStack.getMethodName()
    val fullFunctionStackTrace: String =
      className + "." + methodName

    fullFunctionStackTrace.replace("$", "")
  }

  def logStackTrace(): Unit = {
    for ((thread, _) <- Thread.getAllStackTraces()) {
      if (thread.toString == this.threadToWatch) {
        println(s"Found ${this.threadToWatch}")

        var stackTraceAsList: Seq[String] =
          thread.getStackTrace().reverse.map(getStackInfo).toSeq

        val csvLine: String = stackTraceAsList.mkString(";")
        this.dataFile.write(
          csvLine + " " + stackTraceAsList.length + "\n"
        )
      }
    }
  }

  // Entry point runs by default when put in a new Thread.
  override def run() = {
    try {
      while (true) {
        Thread.sleep(this.stackCheckIntervalInMilliseconds)
        logStackTrace()
      }
    } finally {
      this.dataFile.close()
    }

  }

}
