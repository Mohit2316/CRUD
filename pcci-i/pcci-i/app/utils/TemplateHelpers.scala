package utils

import play.api.templates._

object TemplateHelpers {

  def createRange(page: Int, max: Int, pageCount: Int): Range = {
    val middle: Int = max / 2
    val minNumbering: Int = 1
    val maxNumbering: Int = pageCount
    
    (minNumbering, maxNumbering) match {
//      case (minN, maxN) if maxN <= max && minN <= 0 => 1 to max
//      case (minN, maxN) if maxN > pageCount => minN until pageCount
      case (minN, maxN) => minN to maxN
    }
  }

}