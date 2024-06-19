package org.nlogo.extension.dialog

import java.awt.{ EventQueue => JEventQueue }
import java.lang.{ Boolean => JBoolean, Integer => JInteger }

import org.nlogo.api.{ Argument, Command, Context, DefaultClassManager, Dump, ExtensionException, PrimitiveManager, ReporterRunnable }
import org.nlogo.awt.EventQueue
import org.nlogo.core.{ I18N, LogoList, Syntax }
import org.nlogo.nvm.HaltException
import org.nlogo.swing.{ InputDialog, OptionDialog }
import org.nlogo.window.GUIWorkspace

class DialogExtension extends DefaultClassManager {

  override def load(manager: PrimitiveManager): Unit = {
    manager.addPrimitive("user-input"     , UserInput)
    manager.addPrimitive("user-message"   , UserMessage)
    manager.addPrimitive("user-one-of"    , UserOneOf)
    manager.addPrimitive("user-yes-or-no?", UserYesOrNo)
  }

  private var noDialogIsOpen = true

  private object UserInput extends Command {

    override def getSyntax =
      Syntax.commandSyntax(
        right         = List(Syntax.StringType, Syntax.StringType | Syntax.RepeatableType, Syntax.CommandType)
      , defaultOption = Some(2)
      )

    override def perform(args: Array[Argument], context: Context): Unit = {

      if (noDialogIsOpen && !context.workspace.isHeadless) {
        context.workspace match {
          case gw: GUIWorkspace =>

            val message    = args(0).getString
            val default    = if (args.length > 2) args(1).getString else ""
            val onComplete = args(args.length - 1).getCommand

            var result: AnyRef = null

            val f = {
              () =>

                gw.view.mouseDown(false)

                noDialogIsOpen = false

                result =
                  new InputDialog( gw.getFrame, "User Input", message
                                 , I18N.gui.fn, default
                                 ).showInputDialog()

                noDialogIsOpen = true

            }

            if (JEventQueue.isDispatchThread)
              f()
            else {
              gw.updateUI()
              EventQueue.invokeAndWait { () => f() }
            }

            if (result != null)
              onComplete.perform(context, Array(result))
            else
              throw new HaltException(true)

        }
      }

    }
  }

  private object UserMessage extends Command {

    override def getSyntax =
      Syntax.commandSyntax(
        right         = List(Syntax.StringType, Syntax.CommandType)
      , defaultOption = Some(2)
      )

    override def perform(args: Array[Argument], context: Context): Unit = {

      if (noDialogIsOpen && !context.workspace.isHeadless) {
        context.workspace match {
          case gw: GUIWorkspace =>

            val message    = args(0).getString
            val onComplete = args(1).getCommand

            var result: Int = -1

            val f = {
              () =>

                gw.view.mouseDown(false)

                val okStr   = I18N.gui.get("common.buttons.ok")
                val haltStr = I18N.gui.get("common.buttons.halt")

                noDialogIsOpen = false

                result =
                  OptionDialog.showMessage(gw.getFrame, "User Message", message
                                          , Array(okStr, haltStr))

                noDialogIsOpen = true

            }

            if (JEventQueue.isDispatchThread)
              f()
            else {
              gw.updateUI()
              EventQueue.invokeAndWait { () => f() }
            }

            if (result != 1)
              onComplete.perform(context, Array())
            else
              throw new HaltException(true)

        }
      }

    }
  }

  private object UserOneOf extends Command {

    override def getSyntax =
      Syntax.commandSyntax(
        right         = List(Syntax.StringType, Syntax.ListType, Syntax.CommandType)
      , defaultOption = Some(3)
      )

    override def perform(args: Array[Argument], context: Context): Unit = {

      if (noDialogIsOpen && !context.workspace.isHeadless) {
        context.workspace match {
          case gw: GUIWorkspace =>

            val message    = args(0).getString
            val items      = args(1).getList
            val onComplete = args(2).getCommand

            if (items.isEmpty)
              throw new ExtensionException(I18N.errors.get("org.nlogo.prim.etc.$common.emptyList"))

            val choices = items.map(Dump.logoObject).toArray[AnyRef]

            var result: AnyRef = null

            val f = {
              () =>

                gw.view.mouseDown(false)

                noDialogIsOpen = false

                result =
                  new OptionDialog(gw.getFrame, "User One Of", message, choices
                                  , I18N.gui.fn
                                  ).showOptionDialog()

                noDialogIsOpen = true

            }

            if (JEventQueue.isDispatchThread)
              f()
            else {
              gw.updateUI()
              EventQueue.invokeAndWait { () => f() }
            }

            if (result != null) {
              val index      = result.asInstanceOf[JInteger].intValue
              val resultItem = items(index)
              onComplete.perform(context, Array(resultItem))
            } else {
              throw new HaltException(true)
            }

        }
      }


    }
  }

  private object UserYesOrNo extends Command {

    override def getSyntax =
      Syntax.commandSyntax(
        right         = List(Syntax.StringType, Syntax.CommandType)
      , defaultOption = Some(2)
      )

    override def perform(args: Array[Argument], context: Context): Unit = {

      if (noDialogIsOpen && !context.workspace.isHeadless) {
        context.workspace match {
          case gw: GUIWorkspace =>

            val message    = args(0).getString
            val onComplete = args(1).getCommand

            var result: Int = -1

            val f = {
              () =>

                gw.view.mouseDown(false)

                val yesStr  = I18N.gui.get("common.buttons.yes")
                val noStr   = I18N.gui.get("common.buttons.no")
                val haltStr = I18N.gui.get("common.buttons.halt")

                noDialogIsOpen = false
                result =
                  OptionDialog.showIgnoringCloseBox( gw.getFrame, "User Yes or No", message
                                                   , Array(yesStr, noStr, haltStr), false)
                noDialogIsOpen = true

            }

            if (JEventQueue.isDispatchThread)
              f()
            else {
              gw.updateUI()
              EventQueue.invokeAndWait { () => f() }
            }

            val resultBool =
              result match {
                case 0 => JBoolean.TRUE
                case 1 => JBoolean.FALSE
                case _ => null
              }

            if (resultBool != null)
              onComplete.perform(context, Array(resultBool))
            else
              throw new HaltException(true)

        }
      }

    }
  }

}
