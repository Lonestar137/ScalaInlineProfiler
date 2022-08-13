package com.lonestar.inline

import com.lonestar.inline.job.ProfilerJob

// TODO: add option to specify file path w/ support for Windows
// TODO: add option to specify stackCheckIntervalInMilliseconds
class ThreadProfiler(threadToWatch: String) {
  val profileThread: Thread = new Thread(new ProfilerJob(threadToWatch))

  def startProfile(): Unit = {
    this.profileThread.start()
  }

  def stopProfile(): Unit = {
    this.profileThread.stop()
  }
}
