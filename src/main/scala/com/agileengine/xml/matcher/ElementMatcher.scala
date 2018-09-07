package com.agileengine.xml.matcher

import org.jsoup.nodes.Element

abstract class ElementMatcher {
  def similarityScore(targetElement: Element, element: Element): Int
}

object ElementMatcher {
  def matchers: Seq[ElementMatcher] = {
    List(new AttributeMatcher, new AttributeNameMatcher, new ClassMatcher, new TagMatcher, new TextTokenMatcher)
  }
}
