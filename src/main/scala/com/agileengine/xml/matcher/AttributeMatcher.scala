package com.agileengine.xml.matcher

import org.jsoup.nodes.Element

import scala.collection.JavaConverters._

class AttributeMatcher extends ElementMatcher {

  private val SCORE: Int = 2

  override def similarityScore(targetElement: Element, element: Element): Int = {
    val targetAttributes = targetElement.attributes.asScala.toSet
    val candidateAttributes = element.attributes.asScala.toSet
    val countSameAttribute = targetAttributes.intersect(candidateAttributes).size
    countSameAttribute * SCORE
  }
}
