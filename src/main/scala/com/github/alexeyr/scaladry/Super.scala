package com.github.alexeyr.scaladry

import macrocompat.bundle

import scala.reflect.macros.whitebox

object Super {
  /** When used inside a method definition, resolves to a super call of the same method with the same parameters.
    * Not allowed when any parameter is shadowed by a local variable or import.
    * To specify which `super` to call, see [[superCallOf]].
    */
  def superCall: Any = macro SuperImpl.superCallOf[Nothing]

  /** Same as [[superCall]], but resolves to `super[T]` where [[T]] is the type parameter.
    */
  def superCallOf[T]: Any = macro SuperImpl.superCallOf[T]
}

@bundle
private class SuperImpl(val c: whitebox.Context) {
  import c.universe._

  def superCallOf[T: WeakTypeTag]: Tree = {
    c.internal.enclosingOwner match {
      case method: MethodSymbol =>
        if (method.overrides.isEmpty)
          c.abort(c.enclosingPosition, s"method ${method.name} overrides nothing, superCall makes no sense")
        val args = method.paramLists.map(_.map { sym =>
          val name = sym.asTerm.name.toTermName
          // check that the symbol is not shadowed (comparing positions because == on symbols can produce false
          // negative in this case)
          if (sym.pos == c.typecheck(q"$name").symbol.pos)
            name
          else {
            c.abort(c.enclosingPosition, s"superCall is not allowed because parameter $name is shadowed")
          }
        })
        val typeOfT = weakTypeOf[T]
        val typeParams = method.typeParams.map(_.asType.name)
        if (typeOfT =:= typeOf[Nothing])
          q"super.${method.name.toTermName}[..$typeParams](...$args)"
        else
          q"super[${typeOfT.typeSymbol.name.toTypeName}].${method.name.toTermName}[..$typeParams](...$args)"
      case _ =>
        c.abort(c.enclosingPosition, "superCall must be contained in a method")
    }
  }
}
