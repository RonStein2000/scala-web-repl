package woshilaiceshide.wrepl

import scala.tools.nsc._
import scala.tools.nsc.interpreter._

import woshilaiceshide.wrepl.util.Utility

class Server(interface: String, port: Int, parameters: Seq[NamedParam], settings: Settings = Utility.defaultSettings, max_lines_kept_in_output_cache: Int = 32, repl_max_idle_time_in_seconds: Int = 60) {

  import woshilaiceshide.wrepl.repl._
  import scala.tools.nsc.interpreter.NamedParamClass

  val httpServer: HttpServer = new HttpServer(interface, port, taskRunner => {

    val get_http_server: NamedParamClass = NamedParamClass("get_http_server", "() => woshilaiceshide.wrepl.repl.HttpServer", () => httpServer)

    new Bridge(taskRunner, get_http_server +: parameters, bridge => {
      new PipedRepl(settings, bridge.writer)
    }, max_lines_kept_in_output_cache, repl_max_idle_time_in_seconds)
  })

  def start(asynchronously: Boolean = false) { httpServer.start(asynchronously) }
  def stop(timeout: Int) { httpServer.stop(timeout) }

}
