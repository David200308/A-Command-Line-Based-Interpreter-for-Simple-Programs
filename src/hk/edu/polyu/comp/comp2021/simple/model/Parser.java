package hk.edu.polyu.comp.comp2021.simple.model;

import java.util.*;

/**
 * For control the model Simple.java and Data.java
 */
public class Parser extends Data{

    /**
     * Scanner for retrieving the user input
     */
    private static final Scanner inputLine = new Scanner(System.in);

    /**
     * the result of execution to a string
     */
    protected static String ExecuteResultString = "";

    /**
     * classify the command will go where to process
     * @param command: the commands input by user
     * @param programName: the program name
     */
    public static void classification(String command, String programName) {

        // Classify the commands

        String[] splitStr = command.split(" ");  // Split instruction into words
        String instruction = splitStr[0];


        if (Data.getDebugger().containsKey(programName)){
            if (Data.getDebugger().get(programName).contains(splitStr[1])) Simple.waitDebug(programName);
        }
        
        // Call Commands based on the instruction
        switch (instruction) {
            
            case "vardef":  //* REQ1
                Simple.vardef(splitStr);
                break;
            
            case "binexpr": //* REQ2
                Simple.binExpr(splitStr);
                break;
            
            case "unexpr":  //* REQ3
                Simple.unexpr(splitStr[1], splitStr[2], splitStr[3]);
                break;

            case "assign":  //* REQ4
                Simple.assign(splitStr[2], splitStr[3]);
                break;

            case "print":   //* REQ5   
                Simple.print(splitStr[1], splitStr[2]);
                break;

            case "skip":    //* REQ6
                Simple.skip();
                break;

            case "block":   //* REQ7
                String[] instructions = Arrays.copyOfRange(splitStr, 2, splitStr.length);
                Simple.block(instructions, programName);
                break;
                
            case "if":  //* REQ8
                Simple.ifF(splitStr[2], splitStr[3], splitStr[4], programName);
                break;

            case "while":   //* REQ9
                Simple.whileW(splitStr[2], splitStr[3], programName);
                break;

            case "program": //* REQ10
                Simple.program(splitStr[1], splitStr[2]);
                break;

            case "execute": //* REQ11
                Simple.execute(splitStr[1]);
                System.out.println();
                break;

            case "list":    //* REQ12
                System.out.println("\nList of commands in " + splitStr[1] + ":\n");
                Simple.list(Data.getProgramMap().get(splitStr[1]), new ArrayList<String>());
                break;

            case "store":   //* REQ13
                // Generated by vs-code
                Simple.store(splitStr[1], splitStr[2]);

                break;

            case "load":    //* REQ14
                try { Simple.load(splitStr[1],splitStr[2]);} 
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;

            case "debug":
                Simple.debug(splitStr[1]);
                break;

            case "togglebreakpoint":
                Simple.togglebreakpoint(splitStr[1],splitStr[2]);
                break;

        }

    }

    /**
     * expRef function is check if the expression is a literal, var name or expression name
     * @param expression: the expression statement
     * @return the result of expression
     */
    public static Object expRef(String expression){    // Check if the expression is a literal, var name or expression name.
        
        // If expression is a literal
        if (expression.equals("true")) return true;
        else if (expression.equals("false")) return false;

        
        // If expRef is a variable return variable from map
        if (Data.getVarMap().containsKey(expression)) return Data.getVarMap().get(expression);
            
        // If variable is expression reference
        if (Data.getResultExp().containsKey(expression)) return Data.getResultExp().get(expression);

        try{
            if (Integer.parseInt(expression) > Simple.maxInt) return Simple.maxInt;
            else if (Integer.parseInt(expression) < Simple.minInt) return Simple.minInt;
            else return Integer.parseInt(expression);}
        catch (Exception ignored){
        }

        return null;

    }

    /**
     * inputCMD function is let user input those commands
     * @throws Exception: the exception to handle the input error
     */
    // Use for input command, but just model, Application will be call this
    public void inputCMD() throws Exception {

        String input;

        while(inputLine.hasNextLine()){

            input = inputLine.nextLine();
            if (input != null){
                if (input.equals("quit")) System.exit(0);

                Data.storeCommand(input);

            }

        }

        inputLine.close();
    }

    public static Scanner getScanner() {
        return inputLine;
    }

}