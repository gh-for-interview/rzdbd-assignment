package main

import io.github.shogowada.scala.jsonrpc.serializers.{JSONSerializer, UpickleJSONSerializer}
import io.github.shogowada.scala.jsonrpc.server.JSONRPCServer

import scala.concurrent.ExecutionContext

class JSONRPCServerProvider(implicit ec: ExecutionContext) {

  def buildServer(): JSONRPCServer[UpickleJSONSerializer] = {
    val jsonSerializer = UpickleJSONSerializer()
    val jsonRPCServer = JSONRPCServer(jsonSerializer)

    val dataAggregationAPI: DataAggregationAPI = new DataAggregationAPIImpl()
    jsonRPCServer.bindAPI[DataAggregationAPI](dataAggregationAPI)

    jsonRPCServer
  }
}
