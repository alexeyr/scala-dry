package com.github.alexeyr.scaladry

import utest._
import Super._

object SuperTests extends TestSuite {
  val tests = this {
    'simplestCase {
      class A {
        def foo(x: String) = x.length
      }

      'works {
        class B extends A {
          override def foo(x: String) = superCall + 1
        }

        (new B).foo("") ==> 1
      }

      'mustOverrideSomething {
        compileError("class C extends A { def bar(x: Int) = superCall + 1 }")

        compileError("class D extends A { def foo(x: Int): Int = superCall + 1 }")
      }
    }

    'doubleInheritance {
      trait A {
        def foo(x: String) = x.length
      }

      trait B {
        def foo(x: String) = x.length + 1
      }

      class C extends A with B {
        override def foo(x: String) = superCallOf[A]
      }

      class D extends A with B {
        override def foo(x: String) = superCallOf[B]
      }

      (new C).foo("") ==> 0

      (new D).foo("") ==> 1
    }

    'overloadedMethods {
      class A {
        def foo: Int = 0
        def foo(x: Int): Int = 1
        def foo(x: Double): Int = 2
      }

      class B extends A {
        override def foo: Int = superCall
        override def foo(x: Int): Int = superCall
        override def foo(x: Double): Int = superCall
      }

      val b = new B
      b.foo ==> 0
      b.foo(0) ==> 1
      b.foo(0.0) ==> 2
    }

    'typeParams {
      class Foo[A] {
        def foo[B](x: A, y: B) = y
      }

      class Bar[A] extends Foo[A] {
        override def foo[B](x: A, y: B) = superCall
      }

      class Baz extends Foo[Int] {
        override def foo[A](x: Int, y: A) = superCall
      }

      (new Bar[Int]).foo(0, 0.0) ==> 0.0
      (new Baz).foo(0, 0.0) ==> 0.0
    }

    'multipleParamLists {
      class A {
        def foo(x: Int)(y: String)(implicit z: Int) = x.toString + y + z.toString
      }

      class B extends A {
        override def foo(x: Int)(y: String)(implicit z: Int) = s"B: $superCall"
      }

      implicit val i: Int = 1
      (new B).foo(0)("a") ==> "B: 0a1"
    }

    'shadowedImplicits {
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
