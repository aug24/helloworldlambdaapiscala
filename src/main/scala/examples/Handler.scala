package examples

import java.util.Map

import org.json.simple.JSONObject

trait Handler { def handle(event: JSONObject): Map[String, Object] }
