package ranking.dataaccess

import sttp.client3._
import sttp.client3.circe._
import io.circe.Decoder
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend

object HttpClient {
  private val backend = AsyncHttpClientFutureBackend()

  def get[T](
      endpoint: Endpoint
  )(implicit decoder: Decoder[T]): Future[Either[String, T]] = {
    basicRequest
      .get(uri"${endpoint.url}")
      .response(asJson[T])
      .send(backend)
      .map(
        _.body.fold(
          error => Left(error.getMessage),
          response => Right(response)
        )
      )
  }

  def getText(
      endpoint: Endpoint,
      headers: Map[String, String]
  ): Future[Either[String, String]] = {
    basicRequest
      .get(uri"${endpoint.url}")
      .headers(headers)
      .response(asString)
      .send(backend)
      .map(
        _.body.fold(
          error => Left(error),
          response => Right(response)
        )
      )
  }
}
