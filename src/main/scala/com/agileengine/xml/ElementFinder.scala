package com.agileengine.xml

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}

import scala.collection.JavaConversions._
import scala.collection.immutable.TreeMap
import scala.util.Try

object ElementFinder extends App with LazyLogging {

  private val CHARSET_NAME = "utf8"

  // Jsoup requires an absolute file path to resolve possible relative paths in HTML,
  // so providing InputStream through classpath resources is not a case
  val htmlFile = new File(args(0))
  val candidateFile = new File(args(1))
  val targetElementId = if (args.length > 2) args(2) else "make-everything-ok-button"

  private val targetElement = findElementById(htmlFile, targetElementId).map {ElementMetadataParser.parseMetadata}
  private val document = parseHtmlFile(candidateFile)

  val winner = findSimilarElement(targetElement.get, document)

  logger.info("Path to target element: {}", getElementPath(winner))

  def findElementById(htmlFile: File, targetElementId: String): Option[Element] = Try {
    Jsoup.parse(htmlFile, CHARSET_NAME, htmlFile.getAbsolutePath)
  }.map(_.getElementById(targetElementId)).toOption

  def parseHtmlFile(htmlFile: File): Document =  {
    Jsoup.parse(htmlFile, CHARSET_NAME, htmlFile.getAbsolutePath)
  }

  def getElementPath(element: Element): String = {
    var currentElement = element
    val root = element.root()

    var pathArray = List.newBuilder[String]

    do {
      val current = currentElement

      currentElement = currentElement.parent()

      val nodes = currentElement.childNodes().filter(current.tagName() == _.nodeName())
      if (nodes.size > 1) {
        val tagName = current.tagName()
        val indexOfChildElement = nodes.indexOf(current)
        pathArray += s"$tagName[$indexOfChildElement]"
      }
      else {
        pathArray += currentElement.tagName()
      }

    } while (currentElement != root)

    pathArray.result().reverse.mkString(" > ")
  }

  def findSimilarElement(originalElement: ElementMetadata, document: Document): Element = {
    val allElements = document.getAllElements
    var elementsScores = new TreeMap[Int, Element]()

    allElements.listIterator()
    allElements.foreach(element => {
      val candidate = ElementMetadataParser.parseMetadata(element)
      val score = calculateSimilarityScore(originalElement, candidate)
      logger.info("Element with score {}: {} ({})", score, element.tagName(), element.attributes().toString.trim)
      elementsScores += (score -> element)
    })

    logger.info("Winner {} with score {}", elementsScores.last._2, elementsScores.last._1)
    elementsScores.last._2
  }

  def calculateSimilarityScore(originElement: ElementMetadata, candidate: ElementMetadata): Int = {
    val sameTagScore = 10
    val sameClassScore = 2
    val sameAttributeNameScore = 1
    val sameAttributeValueScore = 2
    val sameTextTokenScore = 1

    var score = 0

    if (originElement.tagName == candidate.tagName) {
      score += sameTagScore
    }

    val countSameClasses = originElement.classes.intersect(candidate.classes).size
    score += (countSameClasses * sameClassScore)

    val countSameAttributeName = originElement.attributes.map(_.getKey).intersect(candidate.attributes.map(_.getKey)).length
    score += (countSameAttributeName * sameAttributeNameScore)

    val countSameAttribute = originElement.attributes.intersect(candidate.attributes).length
    score += (countSameAttribute * sameAttributeValueScore)

    if (originElement.hasText == candidate.hasText) {
      score += 1
    }

    val countSameTextToken = originElement.textTokens.intersect(candidate.textTokens).length;
    score += (countSameTextToken * sameTextTokenScore)

    return score
  }
}
