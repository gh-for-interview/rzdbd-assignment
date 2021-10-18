package main


import scala.concurrent.Future

final case class Data(
               idSample: String,
               numId: String,
               idLocation: String, // место
               idSignalPar: String,
               idDetected: String, // данные о состоянии
               idClassDet: String
               )

final case class AggregatedData(broken: Int, working: Int)

trait DataAggregationAPI {
  def countTypesByLocation(dataList: List[Data]): Future[Map[String, AggregatedData]]
}