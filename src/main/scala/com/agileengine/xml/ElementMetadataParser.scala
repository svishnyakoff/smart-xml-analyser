package com.agileengine.xml

import org.jsoup.nodes.Element

import scala.collection.JavaConverters._

object ElementMetadataParser {

  def parseMetadata(element: Element): ElementMetadata =  {
    val tagName = element.tagName()
    val classes = element.classNames()
    val attributes = element.attributes()
    val hasText = element.hasText
    val textTokens = element.ownText().split("\\W+")

    ElementMetadata(tagName, classes.asScala.toSet, attributes.asList().asScala, hasText, textTokens)
  }
}
