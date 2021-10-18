package main

import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.{as, complete, entity, onComplete, path, post}
import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.StrictLogging
import io.github.shogowada.scala.jsonrpc.serializers.UpickleJSONSerializer
import io.github.shogowada.scala.jsonrpc.server.JSONRPCServer

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class Router(server: JSONRPCServer[UpickleJSONSerializer])(implicit ec: ExecutionContext) extends StrictLogging {

  def buildRoute(): Route = path("rpc") {
    post {
      entity(as[String]) { entity =>
        logger.info("Received request...")
        val f = server.receive(entity)
        onComplete(f) {
          case Failure(exception) =>
            logger.error("Request failed", exception)
            complete(StatusCodes.InternalServerError)
          case Success(Some(value)) =>
            logger.info("Request successfully completed!")
            complete(value)
          case Success(None) =>
            logger.info("Notification successfully completed!")
            complete(StatusCodes.OK)
        }
      }
    }
  }

}
