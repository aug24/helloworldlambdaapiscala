package handlers

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.HashMap
import java.util.Map

import org.json.simple.JSONObject

import examples.Handler

class Part extends Handler {

	private val greeting = "Green Apples"
	
	override def handle(event: JSONObject) = {
		  val response = new HashMap[String, Object]()
	    response.put("message", greeting)
	    val timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime())		
	    response.put("date", timeStamp)
		  response.put("input", event)
		  response
	}

}
