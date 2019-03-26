package examples

import scala.io.Source

trait Router {

    val routeInfo = Source.fromResource("routes").getLines.toList.map{ l =>
      val routeInfo = l.split("\\s+")
      new Route(routeInfo(0), routeInfo(1)) -> Class.forName(routeInfo(2)).asInstanceOf[Class[_ <: Handler]].getDeclaredConstructor().newInstance()
    }
    
    val routes = Map[Route, Handler](routeInfo: _*)
  
	  def getHandler(httpMethod: Option[String], path: Option[String]): Option[Handler] = 
	    (httpMethod, path) match {
      case (Some(a), Some(b)) => routes.get(new Route(a, b))
      case _ => None
    }
    
	
}
