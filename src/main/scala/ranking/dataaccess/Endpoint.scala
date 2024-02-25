package ranking.dataaccess

sealed trait Endpoint {
  val url: String
}

case class Store(category: Category) extends Endpoint {
  private val categoryName: String = category.name
  val url =
    s"https://www.trustpilot.com/_next/data/categoriespages-consumersite-2.38.0/categories/$categoryName.json?sort=latest_review&categoryId=jewelry_store"
}

case class RecentReviews(businessId: String) extends Endpoint {
  val url =
    s"https://www.trustpilot.com/api/categoriespages/$businessId/reviews?locale=en-US"
}

case class StoreTraffic(storeUrl: String, cookieSessionHeader: String) extends Endpoint {
  val url = s"https://vstat.info/$storeUrl"

  private val authorityHeader = "web.vstat.info"


  val headers: Map[String, String] = Map(
    "authority" -> authorityHeader,
    "cookie" -> cookieSessionHeader
  )
}
