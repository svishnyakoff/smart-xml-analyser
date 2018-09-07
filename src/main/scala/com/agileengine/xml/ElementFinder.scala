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
  val htmlFile = new File(getClass.getResource("/sample-0-origin.html").getFile);
//  val candidateFile = new File(getClass.getResource("/startbootstrap-sb-admin-2-examples/sample-1-evil-gemini.html").getFile);
//  val candidateFile = new File(getClass.getResource("/startbootstrap-sb-admin-2-examples/sample-2-container-and-clone.html").getFile);
//  val candidateFile = new File(getClass.getResource("/startbootstrap-sb-admin-2-examples/sample-3-the-escape.html").getFile);
  val candidateFile = new File(getClass.getResource("/startbootstrap-sb-admin-2-examples/sample-4-the-mash.html").getFile);
  val targetElementId = "make-everything-ok-button"

  private val targetElement = findElementById(htmlFile, targetElementId).map {ElementMetadataParser.parseMetadata}
  private val document = parseHtmlFile(candidateFile)

  val winner = findSimilarElement(targetElement.get, document)




  def findElementById(htmlFile: File, targetElementId: String): Option[Element] = Try {
    Jsoup.parse(htmlFile, CHARSET_NAME, htmlFile.getAbsolutePath)
  }.map(_.getElementById(targetElementId)).toOption

  def parseHtmlFile(htmlFile: File): Document =  {
    Jsoup.parse(htmlFile, CHARSET_NAME, htmlFile.getAbsolutePath)
  }

  def findSimilarElement(originalElement: ElementMetadata, document: Document): Element = {
    val allElements = document.getAllElements
    var elementsScores = new TreeMap[Int, Element]()


    allElements.listIterator()
    allElements.foreach(element => {
      val candidate = ElementMetadataParser.parseMetadata(element)
      val score = calculateSimilarityScore(originalElement, candidate)
      logger.info("Element {} has score {}", element, score)
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
