package com.outr.arango.api.model

import io.circe.Json

/**
  * AqlUserfunctionStruct
  *
  * @param code A string representation of the function body
  * @param isDeterministic an optional boolean value to indicate whether the function
  *        results are fully deterministic (function return value solely depends on
  *        the input value and return value is the same for repeated calls with same
  *        input). The *isDeterministic* attribute is currently not used but may be
  *        used later for optimizations.
  * @param name The fully qualified name of the user function
  *
  * WARNING: This code is generated by youi-plugin's generateHttpClient. Do not modify directly.
  */
case class AqlUserfunctionStruct(code: Option[String] = None,
                                 isDeterministic: Option[Boolean] = None,
                                 name: Option[String] = None)