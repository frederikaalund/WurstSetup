package file

import org.kohsuke.args4j.CmdLineException
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import javax.swing.JOptionPane
import javax.swing.JTextArea
import javax.swing.UIManager
import javax.swing.UnsupportedLookAndFeelException

object ExceptionHandler {

    @Throws(IOException::class, CmdLineException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        SetupMain().doMain(args)
    }

    fun setupExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { _, exception ->
            exception.printStackTrace()
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: UnsupportedLookAndFeelException) {
                e.printStackTrace()
            }

            val sw = StringWriter()
            val pw = PrintWriter(sw)
            exception.printStackTrace(pw)
            val jTextField = JTextArea()
            jTextField.text = "Please report this crash with the following info:\nVersion: " + CompileTimeInfo.version + "\n" + sw.toString()
            jTextField.isEditable = false
            JOptionPane.showMessageDialog(null, jTextField, "Sorry, Exception occured :(", JOptionPane.ERROR_MESSAGE)
        }
    }
}