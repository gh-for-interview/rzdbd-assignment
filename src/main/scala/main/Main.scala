package main

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Main extends App with StrictLogging {

  logger.info("Starting app...")

  val config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem("App", config)
  implicit val ec: ExecutionContext = system.dispatcher

  val jsonRPCServerProvider = new JSONRPCServerProvider

  val router = new Router(jsonRPCServerProvider.buildServer())

  Http()
    .bindAndHandle(
      router.buildRoute(),
      interface = config.getString("app.api.interface"),
      port = config.getInt("app.api.port")
    )
    .onComplete {
      case Success(serverBinding) => logger.info(s"Bound app HTTP API to {}", serverBinding.localAddress)
      case Failure(e) => logger.error(s"Unable to bound app HTTP API", e)
    }
}
