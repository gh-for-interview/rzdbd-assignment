package main

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import io.github.shogowada.scala.jsonrpc.client.JSONRPCClient
import io.github.shogowada.scala.jsonrpc.serializers.UpickleJSONSerializer
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Random

class AppSpec extends AnyFlatSpec with Matchers with ScalatestRouteTest {

  private val jsonRPCServerProvider = new JSONRPCServerProvider
  private val route = new Router(jsonRPCServerProvider.buildServer()).buildRoute()
  private val ct = ContentTypes.`application/json`
  private val jsonSerializer = UpickleJSONSerializer()

  "App route" should "work" in {
    val place = "Екатеринбург"
    val data = List(
      Data(
        Random.alphanumeric.take(4).mkString(""),
        Random.alphanumeric.take(10).mkString(""),
        place,
        Random.alphanumeric.take(8).mkString(""),
        "None",
        Random.alphanumeric.take(6).mkString("")
      ),
      Data(
        Random.alphanumeric.take(4).mkString(""),
        Random.alphanumeric.take(10).mkString(""),
        place,
        Random.alphanumeric.take(8).mkString(""),
        "Nan",
        Random.alphanumeric.take(6).mkString("")
      )
    )

    val aggData = Map(place -> AggregatedData(1, 1))

    val jsonSender = { jsonReq: String =>
      var response: Option[String] = None

      Post("/rpc", HttpEntity(ct, jsonReq)) ~> route ~> check {
        response = Some(responseAs[String])
        status shouldEqual StatusCodes.OK
      }
      Future.successful(response)
    }

    val jsonRPCClient = JSONRPCClient(jsonSerializer, jsonSender)
    val dataAggregationAPI = jsonRPCClient.createAPI[DataAggregationAPI]
    val result = Await.result(dataAggregationAPI.countTypesByLocation(data), 5.seconds)
    result shouldBe aggData
  }

  it should "process 100000 entities" in {
      val place = "1223.3232"
      val genData = for {
        _ <- 1 to 100000
      } yield {
        Data(
          Random.alphanumeric.take(4).mkString(""),
          Random.alphanumeric.take(10).mkString(""),
          place,
          Random.alphanumeric.take(8).mkString(""),
          "None",
          Random.alphanumeric.take(6).mkString("")
        )
      }

    val jsonSender = { jsonReq: String =>
      var response: Option[String] = None

      Post("/rpc", HttpEntity(ct, jsonReq)) ~> route ~> check {
        response = Some(responseAs[String])
        status shouldEqual StatusCodes.OK
      }
      Future.successful(response)
    }

    val jsonRPCClient = JSONRPCClient(jsonSerializer, jsonSender)
    val dataAggregationAPI = jsonRPCClient.createAPI[DataAggregationAPI]
    val result = Await.result(dataAggregationAPI.countTypesByLocation(genData.toList), 5.seconds)

    result(place).broken shouldBe 100000
  }
}
