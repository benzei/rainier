package com.stripe.rainier.ir

import com.stripe.rainier.internal.asm.tree.MethodNode

private[ir] case class ApplyClassGenerator(name: String,
                                           classSizeLimit: Int,
                                           outputMethods: Seq[Int],
                                           numInputs: Int,
                                           numGlobals: Int,
                                           numOutputs: Int)
    extends ClassGenerator {

  def superClasses = Array("com/stripe/rainier/ir/CompiledFunction")
  def methods: Seq[MethodNode] =
    List(
      ApplyMethodGenerator(name, classSizeLimit, outputMethods).methodNode,
      createConstantMethod("numInputs", numInputs),
      createConstantMethod("numGlobals", numGlobals),
      createConstantMethod("numOutputs", numOutputs)
    )
}
