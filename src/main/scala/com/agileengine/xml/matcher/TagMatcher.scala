package com.agileengine.xml.matcher

import org.jsoup.nodes.Element

class TagMatcher extends ElementMatcher {

  private val SCORE: Int = 10

  override def similarityScore(targetElement: Element, element: Element): Int = {
    if (targetElement.tagName == element.tagName) SCORE else 0
  }
}
