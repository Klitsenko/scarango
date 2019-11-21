package com.outr.arango.aql

import com.outr.arango.{Document, DocumentModel, DocumentRef, FieldAndValue, Query, Value}

case class UpdatePart[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model], values: List[FieldAndValue[_]]) {
  def build(): Query = {
    val context = QueryBuilderContext()
    val name = context.name(ref)
    var map = Map.empty[String, Value]
    val data = values.map { fv =>
      val arg = context.createArg
      map += arg -> fv.value
      s"${fv.field.name}: @$arg"
    }.mkString(", ")
    Query(s"UPDATE $name WITH {$data} IN ${ref.collectionName}", map)
  }
}