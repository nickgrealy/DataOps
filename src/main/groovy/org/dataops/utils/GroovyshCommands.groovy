package org.dataops.utils

/**
 * Static import commands, that can be run from the console.
 */
class GroovyshCommands {

    static int exec(command){
        execute(command)
    }

    static int execute(command){
        Process proc = command.execute()                 // Call *execute* on the string
        proc.waitFor()                               // Wait for the command to finish
        println "stderr: ${proc.err.text}"
        println "stdout: ${proc.in.text}" // *out* from the external program is *in* for groovy
        proc.exitValue()
    }

}
