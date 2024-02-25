package ranking

import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession
import ranking.ExecutorService.executeScheduledTask
import ranking.dataaccess.DataRequests
import ranking.store.StoreReviewsInMemory

object RankingApp {

  def main(args: Array[String]): Unit = {
    initEnvs()

    val spark = SparkSession
      .builder()
      .appName("RankingApp")
      .master("local[*]")
      .getOrCreate()
    import spark.implicits._

    val store = new StoreReviewsInMemory(Seq.empty)

    executeScheduledTask(() => {
      DataRequests.fetch(reviews => {
        store.updateState(reviews)

        val df = spark.createDataFrame(store.state)
        val reviewsExploded =
          df.select($"*", explode($"reviews").as("review"))

        // Tech task is:
        // Ranking should be done by recent review count (the more reviews the higher) & traffic count
        // (the more traffic the higher). If recent review
        // count is the same, the domain with higher traffic should be shown higher.
        // Also filter by ‘store’ in the category

        reviewsExploded
          .groupBy("businessId")
          .agg(
            count("review").as("recentReviewsCount")
          )
          .join(df, "businessId")
          .select(
            "businessId",
            "name",
            "category",
            "totalReviews",
            "recentReviewText",
            "recentReviewsCount",
            "monthlyVisits"
          )
          .where("category like '%Store%'")
          .orderBy(desc("recentReviewsCount"), desc("monthlyVisits"))
          .limit(10)
          .show()
      })
    })
  }

  private def initEnvs(): Unit = {
    val fetchIntervalMinutes: Option[Int] =
      sys.env.get("FETCH_INTERVAL_MINUTES").map(_.toInt)
    val cookieSessionHeader: Option[String] =
      sys.env.get("COOKIE_SESSION_HEADER")

    (fetchIntervalMinutes, cookieSessionHeader) match {
      case (Some(interval), Some(cookie)) =>
        ExecutorService.repeatInMinutes = interval
        DataRequests.cookieSessionHeader = cookie
      case _ =>
        println(
          "Please provide FETCH_INTERVAL_MINUTES and COOKIE_SESSION_HEADER environment variables"
        )
        System.exit(1)
    }
  }
}
