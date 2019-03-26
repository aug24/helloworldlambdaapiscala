package handlers

import java.util.HashMap
import java.util.Map

import org.json.simple.JSONObject

import examples.Handler

class HealthCheck extends Handler {

	override def handle(event: JSONObject) = new HashMap[String, Object]()

}
