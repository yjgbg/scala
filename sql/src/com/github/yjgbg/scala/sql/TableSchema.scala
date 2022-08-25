package com.github.yjgbg.scala.sql

import java.time.LocalDateTime
import scala.annotation.targetName

object dsl:
  trait Expr[+R,A]
  


  trait Expr[+R,A]:
    def ===[E](column: E)(using Expr[TableSchema.this.type,E]):Criteria[TableSchema.this.type] = ???
  object Expr:
    def derived[A]:Expr[_,A] = ???
    def apply[A](a:A):Expr[_,A] = ???
    def apply[A,TB <:TableSchema](table:TB,column:table.AbstractColumn[_,A]):Expr[TB,A] = ???
  trait Modify[A<:TableSchema]
  trait Criteria[A<:TableSchema]
  trait TableSchema(name:String):
    type Elem[X] = X match
      case AbstractColumn[_,a] => a
    def filter(criteria: Criteria[this.type]):this.type = ???
    def set(closure: Modify[this.type] ?=> Unit):Long = ???
    extension [A,C<:Column[A]](c:C)
      @targetName("eq")
      def :=(a:A):Unit = ???
    def select[T <: Tuple](t: T): Seq[Tuple.Map[T, Elem]] = ???
    trait AbstractColumn[X[_],A]:
      def ===[E](column: E)(using Expr[TableSchema.this.type,E]):Criteria[TableSchema.this.type] = ???
    //      def ===(value:A):Criteria[TableSchema.this.type] = ???
    trait Id[A] extends AbstractColumn[Id,A]:
      def autoIncrease:Id[A] = ???
    trait Column[A] extends AbstractColumn[Column,A]:
      def pk:Id[A] = ???
      def nullable:Column[Option[A]] = ???
    def column[A](name:String):Column[A] = ???

  @main def run = {
    import dsl.*
    object Person extends TableSchema("person"):
      object id extends Id[Long]
      object name extends Column[String]
      object age extends Column[Long]
      object description extends  Column[Option[String]]
      val x = Person
        .filter(Person.id === Person.age)
        .set{
          Person.age := 1
          Person.name := "Alice"
        }
    //        .select(Person.id,Person.name,Person.age)
  }
