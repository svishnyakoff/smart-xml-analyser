package com.agileengine.xml.matcher

import org.jsoup.nodes.Element

import scala.collection.JavaConverters._

class AttributeNameMatcher extends ElementMatcher {

  private val SCORE: Int = 1

  override def similarityScore(targetElement: Element, element: Element): Int = {
    val targetAttributes = targetElement.attributes.asScala.map(_.getKey).toSet
    val candidateAttributes = element.attributes.asScala.map(_.getKey).toSet
    val countSameAttributeName = targetAttributes.intersect(candidateAttributes).size
    countSameAttributeName * SCORE
  }
}
