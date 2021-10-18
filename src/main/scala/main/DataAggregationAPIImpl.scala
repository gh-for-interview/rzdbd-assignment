package main

import com.typesafe.scalalogging.StrictLogging
import scala.concurrent.{ExecutionContext, Future}

class DataAggregationAPIImpl(implicit ec: ExecutionContext) extends DataAggregationAPI with StrictLogging {
  override def countTypesByLocation(dataList: List[Data]): Future[Map[String, AggregatedData]] = Future {
    val start = System.currentTimeMillis()

    val data = dataList.groupBy(_.idLocation).map { case (location, dataList) =>
      val agg = dataList
        .groupBy(_.idDetected)
        .map { case (defect, data) =>
          defect -> data.size
        }
      location -> AggregatedData(
        agg.getOrElse("None", 0),
        agg.getOrElse("Nan", 0)
      )
    }

    logger.info(s"Processing ${dataList.size} entities took ${(System.currentTimeMillis() - start) / 1000} seconds.")
    data
  }
}
