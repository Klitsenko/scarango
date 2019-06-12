package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.{ExecutionContext, Future}
      
object APIIndexgeneral {
  /**
  * **NOTE** Swagger examples won't work due to the anchor.
  * 
  * 
  * Creates a new index in the collection *collection*. Expects
  * an object containing the index details.
  * 
  * The type of the index to be created must specified in the *type*
  * attribute of the index details. Depending on the index type, additional
  * other attributes may need to specified in the request in order to create
  * the index.
  * 
  * Indexes require the to be indexed attribute(s) in the *fields* attribute
  * of the index details. Depending on the index type, a single attribute or
  * multiple attributes can be indexed. In the latter case, an array of
  * strings is expected.
  * 
  * Indexing the system attribute *_id* is not supported for user-defined indexes. 
  * Manually creating an index using *_id* as an index attribute will fail with 
  * an error.
  * 
  * Optionally, an index name may be specified as a string in the *name* attribute.
  * Index names have the same restrictions as collection names. If no value is
  * specified, one will be auto-generated.
  * 
  * Some indexes can be created as unique or non-unique variants. Uniqueness
  * can be controlled for most indexes by specifying the *unique* flag in the
  * index details. Setting it to *true* will create a unique index.
  * Setting it to *false* or omitting the *unique* attribute will
  * create a non-unique index.
  * 
  * **Note**: The following index types do not support uniqueness, and using
  * the *unique* attribute with these types may lead to an error:
  * 
  * - geo indexes
  * - fulltext indexes
  * 
  * **Note**: Unique indexes on non-shard keys are not supported in a
  * cluster.
  * 
  * Hash, skiplist and persistent indexes can optionally be created in a sparse
  * variant. A sparse index will be created if the *sparse* attribute in
  * the index details is set to *true*. Sparse indexes do not index documents
  * for which any of the index attributes is either not set or is *null*. 
  * 
  * The optional attribute **deduplicate** is supported by array indexes of
  * type *hash* or *skiplist*. It controls whether inserting duplicate index values 
  * from the same document into a unique array index will lead to a unique constraint
  * error or not. The default value is *true*, so only a single instance of each
  * non-unique index value will be inserted into the index per document. Trying to
  * insert a value into the index that already exists in the index will always fail,
  * regardless of the value of this attribute.
  */
  def post(client: HttpClient, collection: String, body: Json)(implicit ec: ExecutionContext): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/index", append = true)
    .params("collection" -> collection.toString)
    .restful[Json, Json](body)
}