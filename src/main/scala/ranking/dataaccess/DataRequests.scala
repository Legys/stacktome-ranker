package ranking.dataaccess

import io.circe.Decoder
import ranking.MonthlyVisitParser
import ranking.dataaccess.model.{ReviewResponse, StoreResponse}
import ranking.domain.{Review, StoreWithReviews}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object DataRequests {

  var cookieSessionHeader: String = ""

  private type Error = String
  private type Store = StoreResponse.Response
  private type StoreResult = Either[Error, Store]

  private type StoresWithReviewsFutures =
    Seq[Future[Either[Error, StoreWithReviews]]]

  implicit val storeResponseDecoder: Decoder[StoreResponse.Response] =
    StoreResponse.storeResponseDecoder
  implicit val reviewResponseDecoder: Decoder[ReviewResponse.Response] =
    ReviewResponse.reviewResponseDecoder

  private val jewelryStores = Store(JewelryStore())
  private val furnitureStores = Store(FurnitureStore())
  private val clothingStores = Store(ClothingStore())

  private val jewelryStoreRequest =
    HttpClient.get(jewelryStores)(storeResponseDecoder)
  private val furnitureStoreRequest =
    HttpClient.get(furnitureStores)(storeResponseDecoder)
  private val clothingStoreRequest =
    HttpClient.get(clothingStores)(storeResponseDecoder)

  private val storesFutures =
    Seq(jewelryStoreRequest, furnitureStoreRequest, clothingStoreRequest)

  private def fetchReviewsByBusiness(
      business: StoreResponse.Business
  ): Future[Either[Error, ReviewResponse.Response]] = {
    val reviewsRequest = RecentReviews(business.businessUnitId)

    HttpClient
      .get(reviewsRequest)(reviewResponseDecoder)
  }

  private def fetchStoreTraffic(
      business: StoreResponse.Business
  ): Future[Either[Error, Int]] = {
    val trafficRequest =
      StoreTraffic(business.identifyingName, cookieSessionHeader)

    HttpClient
      .getText(trafficRequest, trafficRequest.headers)
      .map(result =>
        result.map(html =>
          // Returns 0 if the monthly visits are not found.
          // Not error, because it's an additional metric.
          // In this way we recover if one of the stores fails
          // to provide the monthly visits.
          MonthlyVisitParser.extractMonthlyVisits(html).fold(0)(identity)
        )
      )
  }

  private def createStoreWithReviews(
      category: String,
      business: StoreResponse.Business,
      reviews: Seq[ReviewResponse.Review],
      monthlyVisits: Int
  ): StoreWithReviews = {
    StoreWithReviews(
      businessId = business.businessUnitId,
      category = category,
      name = business.displayName,
      reviews = reviews.map(review =>
        Review(review.id, review.text, businessId = business.businessUnitId)
      ),
      recentReviewText = reviews.headOption.map(_.text).getOrElse(""),
      totalReviews = business.numberOfReviews,
      monthlyVisits = monthlyVisits
    )
  }
  private def toStoreWithReviews(
      category: String,
      business: StoreResponse.Business,
      reviewResponse: Future[Either[Error, ReviewResponse.Response]],
      trafficResponse: Future[Either[Error, Int]]
  ): Future[Either[Error, StoreWithReviews]] = {

    val response = for {
      reviews <- reviewResponse
      traffic <- trafficResponse
    } yield (reviews, traffic)

    response.map {
      case (Right(reviews), Right(traffic)) =>
        Right(
          createStoreWithReviews(category, business, reviews.reviews, traffic)
        )
      case (Left(error), _) => Left(error)
      case (_, Left(error)) => Left(error)
    }
  }

  private def mapStoresToReviewsFutures(
      storesResults: Seq[StoreResult]
  ): StoresWithReviewsFutures = {
    storesResults.flatMap(storeResult =>
      storeResult.toSeq.flatMap(store =>
        store.pageProps.businessUnits.businesses.map { business =>
          toStoreWithReviews(
            store.pageProps.categories.currentCategory.displayName,
            business,
            fetchReviewsByBusiness(business),
            fetchStoreTraffic(business)
          )
        }
      )
    )
  }

  // It's simplified for the sake of the example
  private def logError(error: Error): Unit = {
    println("Error fetching data:")
    println(error)
  }
  def fetch(retrieveResponse: Seq[StoreWithReviews] => Unit): Unit = {

    val storesWithReviews = for {
      stores <- Future.sequence(storesFutures)
      storesWithReviews <- Future.sequence(mapStoresToReviewsFutures(stores))
    } yield storesWithReviews

    storesWithReviews.onComplete {
      case Success(stores) =>
        retrieveResponse(stores.collect { case Right(store) => store })
      case Failure(error) => logError(error.getMessage)
    }
  }
}
