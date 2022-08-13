import java.io.FileWriter
import scala.collection.JavaConversions.mapAsScalaMap

class ProfilerJob(threadToWatch: String = "Thread[main,5,main]")
    extends Runnable {

  val stackCheckInterval = 1000
  var stackMap = Map[String, List[StackTraceElement]]()
  val dataFile: FileWriter = new FileWriter(
    "src/main/resources/profile_data.csv",
    true
  )

  // String = methodName, Int = occurrences * stackCheckInterval = time / 1000 = seconds

  def getStackInfo(threadStack: StackTraceElement): String = {
    val className: String = threadStack.getClassName()
    val methodName: String = threadStack.getMethodName()
    val fullFunctionStackTrace: String =
      className + "." + methodName

    fullFunctionStackTrace.replace("$", "")
  }

  def checkStack(): Unit = {
    for ((thread, _) <- Thread.getAllStackTraces()) {
      if (thread.toString == this.threadToWatch) {
        println(s"Found ${this.threadToWatch}")

        var csvLine: String = thread.toString() + ", "
        thread.getStackTrace().reverse.foreach {
          stackTraceLayer: StackTraceElement =>
            csvLine += getStackInfo(stackTraceLayer) + ", "
        }
        println("Line: " + csvLine)
        this.dataFile.append(csvLine + "\n")
        // TODO append the csvLine to a .csv file in resources.

      }
    }
  }

  // Entry point runs by default when put in a new Thread.
  override def run() = {
    try {
      while (true) {
        println("running")
        Thread.sleep(stackCheckInterval)
        checkStack()
      }
    } finally {
      this.dataFile.close()
    }

  }

}

class ThreadProfiler(threadToWatch: String) {
  val profileThread: Thread = new Thread(new ProfilerJob(threadToWatch))

  def startProfile(): Unit = {
    this.profileThread.start()
  }

  def stopProfile(): Unit = {
    this.profileThread.stop()
  }

}

object FlameGrapher {}

object Main {
  def test() {
    println("helloworld")
    def layer2() {
      "xd"

      Thread.sleep(1000)
      def layer3() {
        Thread.sleep(1000)
        10
      }
      layer3()
    }
    layer2()
  }

  def main(args: Array[String]): Unit = {
    val mainThreadName: String = Thread.currentThread().toString()

    val x = new ThreadProfiler(mainThreadName)
    x.startProfile()

    List(1, 2, 3).foreach { x =>
      Thread.sleep(1000)
      test()
    }

    println("Done")

    x.stopProfile()
  }

}
