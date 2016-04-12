package com.github.alexeyr.scaladry

import macrocompat.bundle

import scala.reflect.macros.whitebox

object Super {
  /** When used inside a method definition, resolves to a super call of the same method with the same parameters.
    * Note that this includes implicit parameters, even if they are shadowed in this scope.
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
        val args = method.paramLists.map(_.map(sym => c.Expr(q"$sym")))
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
