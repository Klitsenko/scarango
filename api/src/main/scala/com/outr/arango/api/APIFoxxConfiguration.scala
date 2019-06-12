package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIFoxxConfiguration {
  /**
  * Fetches the current configuration for the service at the given mount path.
  * 
  * Returns an object mapping the configuration option names to their definitions
  * including a human-friendly *title* and the *current* value (if any).
  */
  def get(client: HttpClient, mount: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/foxx/configuration", append = true) 
    .params("mount" -> mount.toString)
    .call[Json]

  /**
  * Replaces the given service's configuration.
  * 
  * Returns an object mapping all configuration option names to their new values.
  */
  def patch(client: HttpClient, body: Json, mount: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Patch)
    .path(path"/_api/foxx/configuration", append = true) 
    .params("mount" -> mount.toString)
    .restful[Json, Json](body)

  /**
  * Replaces the given service's configuration completely.
  * 
  * Returns an object mapping all configuration option names to their new values.
  */
  def put(client: HttpClient, body: Json, mount: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Put)
    .path(path"/_api/foxx/configuration", append = true) 
    .params("mount" -> mount.toString)
    .restful[Json, Json](body)
}