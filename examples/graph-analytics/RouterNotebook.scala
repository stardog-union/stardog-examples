// Databricks notebook source
import com.stardog.spark.GraphAnalytics

val sgServer = "https://solutions-demo.stardog.cloud:5820"
val pw = dbutils.secrets.get("vgtest-databricks-secret-scope", "enterprise-demo-pw-cjd")
val userName = dbutils.secrets.get("vgtest-databricks-secret-scope", "enterprise-demo-cjd")
val dbName = "router"




// COMMAND ----------

val q = "construct {?r1 ?p ?r2 .} where {?r1 a net:Router; ?p ?r2 . ?r2 a net:Router . }"

// COMMAND ----------

val params = Array(
  "algorithm.name=StronglyConnectedComponents",
  "algorithm.iterations=5",
  "stardog.server=" + sgServer,
  "stardog.database=" + dbName,
  "stardog.username=" + userName,
  "stardog.password=" + pw,
  "stardog.query.timeout=10m",
  "stardog.reasoning.schema=onto",
  "stardog.query=" + q,
  "output.property=http://routers.stardog.com/symComp/component",
  "output.graph=http://routers.stardog.com/ontoComp",
  "spark.dataset.size=12000"
)

// COMMAND ----------

GraphAnalytics.main(params)

// COMMAND ----------


