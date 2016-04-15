// Doesn't compile under 2.10 with an assertion error in the typechecker
// See ../scala-2.10/SuperTests210.scala

package com.github.alexeyr.scaladry

import utest._
import Super._

object SuperTests211 extends TestSuite {
  val tests = this {
    'shadowingGivesCompileError {
      compileError(
        """class A {
           def foo(x: Int, z: Int) = x.toString + z.toString
         }
         class C extends A {
           override def foo(x: Int, z: Int) = {
             // shadows z from parameter list
             implicit val z: Int = 2
             "C: " + superCall
           }
         }""")
    }
  }
}
