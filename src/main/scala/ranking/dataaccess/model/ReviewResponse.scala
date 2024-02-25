package ranking.dataaccess.model

import io.circe._, io.circe.generic.semiauto._
import io.circe.generic.auto._

object ReviewResponse {

  case class Date(
      createdAt: String
  )

  case class Review(
      id: String,
      text: String,
      date: Date
  )

  case class Response(
      reviews: Seq[Review]
  )

  implicit val reviewResponseDecoder: Decoder[Response] = deriveDecoder
}
