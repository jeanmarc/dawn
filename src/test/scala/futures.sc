import java.util.concurrent.TimeoutException

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scala.concurrent.duration._
// futures.sc
// Worksheet to explore working with collections of futures and different ways of combining them.
// Trying to stay pure Scala and not use the Scalaz's and Cats's of the world.

// Situation 1
// We have a bunch of work to perform, and eventually want to collect the results, and the failures too.

implicit val ec = ExecutionContext.global
val rnd = new scala.util.Random()

def workPackage(name: String, mayFail: Boolean = false, minTime: Int = 10, maxTime: Int = 2000): Future[String] = {
  Future {
    val delay = minTime + rnd.nextInt(maxTime - minTime)
    Thread.sleep(delay)
    if (mayFail) {
      if (rnd.nextInt(2) == 0) {
        throw new ArithmeticException("bork")
      }
    }
    s"$name is done"
  }

}

val w1: Future[String] = workPackage("a", true)
val w2: Future[String] = workPackage("b", true)
val w3: Future[String] = workPackage("c", true)

val theList = List(w1, w2, w3)

// A future can fail (either by exception thrown in the calculation, or a timeout). To capture the individual results,
// Each Future[String] in the List will become a Try[String] in the List, which will be delivered in the Future[List]

// helper method to convert a Future[T] to Future[Try[T]]
def futureToFutureTry[T](f: Future[T]): Future[Try[T]] =
  f.map(Success(_)).recover({ case x: Throwable => Failure(x)})

// To go from List[Future[X]] to Future[List[X]], one uses Future.sequence(...)

// create a Future[Try[List[Try[String]]]] from the List[Future[String]]
val myResult = Future.sequence(theList.map(futureToFutureTry(_)))

// observe how the futures complete over time

myResult
w1
w2
w3

try {
  Await.result(myResult, 1.seconds)
} catch {
  case x: TimeoutException => "Timeout occurred"
}

myResult
w1
w2
w3

try {
  Await.result(myResult, 1.seconds)
} catch {
  case x: TimeoutException => "Timeout occurred"
}

myResult
w1
w2
w3

val theEnd = try {
  Await.result(myResult, 1.seconds)
} catch {
  case x: TimeoutException => List.empty
}
