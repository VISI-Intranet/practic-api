import com.typesafe.config.ConfigFactory


val c = ConfigFactory.load("service_app.conf")
val s = c.getString("service.serviceName")