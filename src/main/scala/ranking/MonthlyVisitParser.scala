package ranking

import scala.util.Try

object MonthlyVisitParser {
  def extractMonthlyVisits(html: String): Option[Int] = {
    val pattern =
      """<span class="time-dot-sum" id="MONTHLY_VISITS" data-datum="(\d+)" """.r
    pattern.findFirstMatchIn(html) match {
      case Some(m) => Try(m.group(1).toInt).toOption
      case None    => None
    }
  }
}
