package com.agileengine.xml.matcher
import org.jsoup.nodes.Element

import scala.collection.JavaConverters._

class ClassMatcher extends ElementMatcher {

  private val SCORE: Int = 2
  override def similarityScore(targetElement: Element, element: Element): Int = {
    val countSameClasses = targetElement.classNames.asScala.intersect(element.classNames().asScala).size
    countSameClasses * SCORE
  }
}
