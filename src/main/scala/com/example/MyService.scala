package com.example

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import org.apache.spark.{SparkConf, SparkContext}

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(totalRoute ~ simpleWordCountRoute)
}


// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  val conf = new SparkConf().setAppName("spray-spark-test").setMaster("local")
  val sc = new SparkContext(conf)

  val totalRoute =
    path("total") {
      get {
        respondWithMediaType(`text/html`) {
          // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <p>Word Count:
                  {totalWordCount}
                </p>
              </body>
            </html>
          }
        }
      }
    }


  val simpleWordCountRoute =
    path("distinct") {
      get {
        respondWithMediaType(`text/html`) {
          // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                {simpleWordCount.collect().map {
                case (str, count) => <li>
                  <em>
                    {str}
                    :</em>{count}
                </li>
              }}
              </body>
            </html>
          }
        }
      }
    }

  def simpleWordCount = {
    val userDir = System.getProperty("user.dir")
    sc.textFile(s"file:///$userDir/sample.txt")
      .flatMap(_.split("\\s").map((_, 1))).reduceByKey(_ + _)
  }

  def totalWordCount = {
    val userDir = System.getProperty("user.dir")
    sc.textFile(s"file:///$userDir/sample.txt")
      .map(_.split("\\s").length).sum()
  }

}