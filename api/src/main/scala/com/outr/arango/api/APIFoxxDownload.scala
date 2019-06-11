package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIFoxxDownload {
  /**
  * Downloads a zip bundle of the service directory.
  * 
  * When development mode is enabled, this always creates a new bundle.
  * 
  * Otherwise the bundle will represent the version of a service that
  * is installed on that ArangoDB instance.
  */
  def post(client: HttpClient, mount: String)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/foxx/download", append = true) 
    .params("mount" -> mount.toString)
    .call[Json]
}