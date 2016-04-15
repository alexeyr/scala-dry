// Uncomment to verify the expected compilation error

//package com.github.alexeyr.scaladry
//
//import utest._
//import Super._
//
//object SuperTests210 extends TestSuite {
//  val tests = this {
//    'shadowingGivesCompileError {
//      class A {
//        def foo(x: Int, z: Int) = x.toString + z.toString
//      }
//      class C extends A {
//        override def foo(x: Int, z: Int) = {
//          // shadows z from parameter list
//          implicit val z: Int = 2
//          "C: " + superCall
//        }
//      }
//    }
//  }
//}
