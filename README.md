# scala-dry
Scala macros which help you not to repeat yourself

[![Build Status](https://travis-ci.org/alexeyr/scala-dry.svg?branch=master)](https://travis-ci.org/alexeyr/scala-dry)

## `super` calls
Most often `super` calls use the same method name and arguments, simply doing something before or after the call
or changing the result in some way, something like
    
    class HelloAndroid extends Activity {
      override def onCreate(savedInstanceState: Bundle): Unit = {
        super.onCreate(savedInstanceState)
        ...
      }
    }
    
Using `com.github.alexeyr.scaladry.Super.superCall`, you can write the above as
 
    class HelloAndroid extends Activity {
      override def onCreate(savedInstanceState: Bundle): Unit = {
        superCall
        ...
      }
    }

instead. Obviously in this case the win isn't large, because `onCreate` signature is quite unlikely to change,
but for methods in your own classes or third-party libraries, whenever method signature changes, the `super` calls need
to be changed as well. If you forget to do so, you can unintentionally call an overloaded version, e.g.

    // Was:
    class A {
      def foo(): Int = ...
    }
    
    class B extends A {
      override def foo() = super.foo() + 1
    }
    
    // Became:
    class A {
      def foo(x: Int): Int = ...
      def foo(): Int = foo(0)
    }
    
    class B extends A {
      override def foo(x: Int) = super.foo() + 1 // should have been super.foo(x) + 1
    }

If there are several overridden methods and you need to specify which one to call, use `superCallOf[A]` instead.