package ranking.dataaccess.model

import io.circe._
import io.circe.generic.semiauto._
import io.circe.generic.auto._

import java.time.ZonedDateTime

object ReviewResponse {

  case class Date(
      createdAt: ZonedDateTime
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
