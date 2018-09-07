package com.agileengine.xml.matcher

import org.jsoup.nodes.Element

class TextTokenMatcher extends ElementMatcher {

  private val SCORE: Int = 1

  override def similarityScore(targetElement: Element, element: Element): Int = {
    val targetTextTokens = targetElement.ownText().split("\\W+")
    val candidateTextTokens = element.ownText().split("\\W+")
    val countSameTextToken = targetTextTokens.intersect(candidateTextTokens).length;
    countSameTextToken * SCORE
  }
}
