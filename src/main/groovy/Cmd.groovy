import static Cmd.*

class Cmd {

    static int exec(command){
        Process proc = command.execute()                 // Call *execute* on the string
        proc.waitFor()                               // Wait for the command to finish
        println "stderr: ${proc.err.text}"
        println "stdout: ${proc.in.text}" // *out* from the external program is *in* for groovy
        proc.exitValue()
    }

    public static void main(String[] args) {
        exec 'ls'
    }
}
