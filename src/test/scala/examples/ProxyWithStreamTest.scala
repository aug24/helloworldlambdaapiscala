package examples

import scala.collection.Map
import scala.collection.JavaConversions._

import org.json.simple.JSONObject

import org.scalatest.FlatSpec

class ProxyWithStreamTest extends FlatSpec {
    /**
     * We would usually have a bunch of tests that the input was interpreted correctly etc
     */
	"The Proxy" should "give a clean non-empty relative path" in {
		val parameters = Map[String, Object]("path" -> "/mypath")
		val path = new ProxyWithStream().getPath(new JSONObject(parameters))
		assert (path == Some("mypath")) 
	}

	"The Proxy" should "give a clean non-empty path" in {
		val parameters = Map[String, Object]("path" -> "mypath")
		val path = new ProxyWithStream().getPath(new JSONObject(parameters))
		assert (path == Some("mypath"))
	}

	"The Proxy" should "give no path" in {
		val parameters = Map[String, Object]()
		val path = new ProxyWithStream().getPath(new JSONObject(parameters))
		assert (path == None)
	}
	
	"The Proxy" should "find the correct route if exists" in {
	  val httpMethod = Some("GET")
	  val path = Some("user")
	  val methodName = new ProxyWithStream().getHandler(httpMethod, path).get.getClass.getName
	  assert ( methodName == "handlers.User")
	}
	
	
}
