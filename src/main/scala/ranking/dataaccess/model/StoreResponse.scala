package ranking.dataaccess.model

import io.circe._, io.circe.generic.semiauto._
import io.circe.generic.auto._

object StoreResponse {

  case class Category(
      categoryId: String,
      displayName: String
  )
  case class CurrentCategory(
      currentCategory: Category
  )
  case class Business(
      businessUnitId: String,
      displayName: String,
      identifyingName: String,
      numberOfReviews: Int
  )

  case class BusinessUnits(
      businesses: Seq[Business]
  )
  case class PageProps(
      businessUnits: BusinessUnits,
      categories: CurrentCategory
  )
  case class Response(
      pageProps: PageProps
  )

  implicit val storeResponseDecoder: Decoder[Response] = deriveDecoder
}
