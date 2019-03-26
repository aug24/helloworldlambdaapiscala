package handlers

import java.util.HashMap
import java.util.Map
import scala.collection.JavaConversions._

import org.json.simple.JSONObject

import org.scalatest._

class PartTest extends FlatSpec {
    /**
     * Check that the original input is returned.  This is used for debugging in the client.
     */
    "A part" should "preserve original input" in {
      	val request = new HashMap[String, Object]()
      	request.put("one", "two")
      	val response = new Part().handle(new JSONObject(request))
        assert(response.containsKey("input"))
        assert((response.get("input").asInstanceOf[JSONObject]).containsKey("one"))
    }

    /**
     * Check that the message is sent back 
     */
    "A part" should "include a message" in {
    	  val response = new Part().handle(new JSONObject())
        assert(response.containsKey("message"))
    }

    /**
     * Check that the message is sent back correctly
     */
    "A part" should "include correct message" in {
    	  val response = new Part().handle(new JSONObject())
        assert(response.get("message").toString().equals("Green Apples"))
    }

}
