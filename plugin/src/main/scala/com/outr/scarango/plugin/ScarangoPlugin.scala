package com.outr.scarango.plugin

import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{Files, Path}

import sbt.{file, _}
import Keys._

import scala.collection.JavaConverters._
import scala.reflect.internal.util.ScalaClassLoader.URLClassLoader

object ScarangoPlugin extends sbt.AutoPlugin {
  override def trigger: PluginTrigger = allRequirements

  object autoImport {
    lazy val updateModels = TaskKey[Unit]("updateModels", "Creates or updates DocumentModel companions for Document case classes")
  }

  import autoImport._

  override lazy val projectSettings: Seq[Def.Setting[_]] = Seq(
    updateModels := {
      val classPath = fullClasspath.in(Runtime).value
      val urls = classPath.map(_.data.toURI.toURL)
      val classLoader = new URLClassLoader(urls, null)
      val dir = classPath.head.data.toPath
      val documentTrait = classLoader.loadClass("com.outr.arango.Document")
      val classes: List[String] = Files.find(dir, Int.MaxValue, (path: Path, _: BasicFileAttributes) => {
        path.toString.toLowerCase.endsWith(".class")
      })
        .iterator()
        .asScala
        .toList
        .map(dir.relativize)
        .map(_.toString.replace('/', '.'))
        .map(c => c.substring(0, c.length - 6))
        .filterNot(_.endsWith("$"))
        .distinct
      classes.foreach { c =>
        val clazz = classLoader.loadClass(c)
        val isDocument = documentTrait.isAssignableFrom(clazz)
        if (isDocument && !clazz.isInterface) {
          import scala.reflect.runtime.universe._

          val m = runtimeMirror(classLoader)

          // TODO: support recursively built fields list, breaking into sub-objects supporting Option[case class] and Seq[case class]
          val classSymbol = m.classSymbol(clazz)
          val classMirror = m.reflectClass(classSymbol)
          val moduleSymbol = classMirror.symbol.companion
          val apply = moduleSymbol
            .typeSignature
            .decls
            .filter(_.isMethod)
            .filter(_.asMethod.name.toTermName == TermName("apply"))
            .map(_.asMethod)
            .last
          println(s"Found: $clazz")
          val paths = sourceDirectories.in(Runtime).value
          val directories = paths.map(p => new File(p, clazz.getPackage.getName.replace('.', '/')))
          directories.map(new File(_, s"${clazz.getSimpleName}.scala")).find(_.exists()) match {
            case Some(file) => {
              println(s"File: $file")
              val source = IO.read(file)
              /*
              object Stream extends DocumentModel[Stream] {
  override val collectionName: String = "streams"
  override implicit val serialization: Serialization[Stream] = Serialization.auto[Stream]
}
               */
              val obj = extractObject(clazz.getSimpleName, source)
              println(s"Object: [$obj]")
            }
            case None => println(s"No file found for $clazz")
          }
          // TODO: Find source code file
          /*val params = apply.paramLists.head.map(_.asTerm)
          val fields = params.map { p =>
            val `type` = p.typeSignature.resultType.toString.replaceAllLiterally("Predef.", "")
            object CaseField {
              def unapply(trmSym: TermSymbol): Option[(Name, Type)] = {
                if (trmSym.isVal && trmSym.isCaseAccessor)
                  Some((TermName(trmSym.name.toString.trim), trmSym.typeSignature))
                else
                  None
              }
            }
            val caseEntries = p.typeSignature.resultType.decls.collect {
              case CaseField(nme, tpe) => (nme, tpe)
            }.toList
            val name = p.name.decodedName.toString
            val encName = encodedName(name)
            if (caseEntries.isEmpty) {
              s"""val $encName: Field[${`type`}] = Field[${`type`}]("$name")"""
            } else {
              val subFields = caseEntries.map {
                case (nme, tpe) => {
                  val subType = tpe.toString.replaceAllLiterally("Predef.", "")
                  s"""val ${encodedName(nme.decodedName.toString)}: Field[$subType] = Field[$subType]("$name.$nme")"""
                }
              }
              s"""object $encName extends Field[${`type`}]("$name") {
                 |    ${subFields.mkString("\n    ")}
                 |  }""".stripMargin
            }
          }
          val source =
            s"""
               |package ${clazz.getPackage.getName}
               |
               |import com.outr.giantscala._
               |
               |/**
               | * WARNING: This file was generated by giant-scala's SBT plugin generateDBModels. Do not edit directly.
               | */
               |abstract class ${clazz.getSimpleName}Model(collectionName: String, db: MongoDatabase) extends DBCollection[${clazz.getSimpleName}](collectionName, db) {
               |  override val converter: Converter[${clazz.getSimpleName}] = Converter.auto[${clazz.getSimpleName}]
               |
               |  ${fields.mkString("\n  ")}
               |}
             """.stripMargin.trim
          val sourceDir = new File(outDirectory, clazz.getPackage.getName.replace('.', '/'))
          val sourceFile = new File(sourceDir, s"${clazz.getSimpleName}Model.scala")
          val sourcePath = sourceFile.toPath
          Files.deleteIfExists(sourcePath)
          Files.createFile(sourcePath)
          Files.write(sourcePath, source.getBytes)
          println(s"Created ${sourceFile.getCanonicalPath} successfully")*/
        }
      }
    }
  )

  private def encodedName(name: String): String = name match {
    case "type" => "`type`"
    case _ => name
  }

  private def extractObject(className: String, source: String): String = {
    val start = source.indexOf(s"object $className")
    val b = new StringBuilder
    var open = List.empty[Char]
    var started = false
    var ended = false
    source.substring(start).foreach { c =>
      if (!ended) {
        if (c == '"') {
          if (open.headOption.contains('"')) {
            open = open.tail
          } else {
            open = c :: open
          }
        } else if (c == '{' && !open.headOption.contains('"')) {
          started = true
          open = c :: open
        } else if (c == '}' && !open.headOption.contains('"')) {
          open = open.tail
          if (open.isEmpty) ended = true
        }
        b.append(c)
      }
    }
    b.toString()
  }
}

case class ModelDetails(className: String, args: List[ModelArg], path: Path, packageName: String) {
  override def toString: String = s"$className(${args.mkString(", ")})"
}

case class ModelArg(name: String, `type`: String) {
  override def toString: String = s"$name: ${`type`}"
}