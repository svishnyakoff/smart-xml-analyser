package com.agileengine.xml

import org.jsoup.nodes.Attribute

case class ElementMetadata(tagName: String, classes: Set[String], attributes: Seq[Attribute],
                           hasText: Boolean, textTokens: Seq[String]) {

}
