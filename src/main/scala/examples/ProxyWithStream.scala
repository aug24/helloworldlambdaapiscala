package examples

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.HashMap
import java.util.Map
import java.io.BufferedReader

import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger

import org.json.simple.JSONObject
import org.json.simple.parser.ParseException
import org.json.simple.parser.JSONParser

import handlers.HealthCheck
import handlers.Part
import handlers.User

class ProxyWithStream extends RequestStreamHandler with Router {

    val parser = new JSONParser()

    def handleRequest(inputStream: InputStream, outputStream: OutputStream, context: Context):Unit = {

        val logger = context.getLogger()
        logger.log("Loading Java Lambda handler of ProxyWithStream")

        val reader = new BufferedReader(new InputStreamReader(inputStream))
        val responseBody = getResponse(reader, logger)        
        logger.log(responseBody)
        
        val writer = new OutputStreamWriter(outputStream, "UTF-8")
        writer.write(responseBody)  
        writer.close()
    }
    
    def getResponse(reader: BufferedReader, logger: LambdaLogger): String = {
    	val response: Map[String, Object] =
        try {
            val event = parser.parse(reader).asInstanceOf[JSONObject]
            logger.log(event.toJSONString())

            val path = getPath(event)
            val httpMethod = getHttpMethod(event)
        	  logger.log(httpMethod + " request to " + path)

            getMethod(httpMethod, path, event, logger)
        } catch {
          case pex: ParseException => 
        	  val failresponse = new HashMap[String, Object]()
            failresponse.put("statusCode", "400")
            failresponse.put("exception", pex)
            failresponse
        }
      new JSONObject(response).toJSONString()
    }

    def getPath(event: JSONObject) = Option(event.get("path")).map(_.toString).map(_.replaceAll("^/+", "")) 
    
	  def getHttpMethod(event: JSONObject) = Option(event.get("httpMethod")).map(_.toString)
    
    def getMethod(httpMethod: Option[String], path: Option[String], event: JSONObject, logger: LambdaLogger): Map[String, Object] =
        if (event.containsKey("requestContext"))
        	  getALBMethod(httpMethod, path, event, logger)
    	  else
	    	    getAPIGMethod(httpMethod, path, event, logger)
    
    // This could be better rewritten as a map or something.  In Scala, we'd use Map.getOrElse.
    def getAPIGMethod(httpMethod: Option[String], path: Option[String], event: JSONObject, logger: LambdaLogger) = {
      	logger.log("Event does not have 'requestContext' key assuming API Gateway")
      	
      	getHandler(httpMethod, path) match {
        	  case Some(a) => a.handle(event)
        	  case None => {
        		    val response = new HashMap[String, Object]()
        			  response.put("statusCode", 400.asInstanceOf[Object])
        			  response.put("statusDescription", "400 Client Error")
        			  response
        	  }
      	}
    }

    // This could be better rewritten as a map or something.  In Scala, we'd use Map.getOrElse.
    def getALBMethod(httpMethod: Option[String], path: Option[String], event: JSONObject, logger: LambdaLogger) = {
      	logger.log("Event has 'requestContext' key assuming ALB")
      	
      	val response = new HashMap[String, Object]()
  		  response.put("isBase64Encoded", false.asInstanceOf[Object])
  		  val headers = new HashMap[String, Object]()
  		  headers.put("Content-Type", "application/json")
  		  response.put("headers", new JSONObject(headers)) 
  
      	getHandler(httpMethod, path) match {
        	  case Some(a) => {
          		  response.put("statusCode", 200.asInstanceOf[Object])
          		  response.put("statusDescription", "200 OK")
            	  response.put("body", new JSONObject(a.handle(event)).toJSONString())
        	  }
        	  case None => {
      	    	  response.put("statusCode", 400.asInstanceOf[Object])
	            	response.put("statusDescription", "400 Client Error")
	      	      val failbody = new HashMap[String, Object]()
	      	      failbody.put("error", "Unknown path: " + path)
	      	      response.put("body", new JSONObject(failbody).toJSONString())
        	  }
      	}

        response
    }
}
