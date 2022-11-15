package hk.edu.polyu.comp.comp2021.simple.model;

import java.io.IOException;
import java.util.*;

public class Parser {
    /**
     * storeCommand function will split those command and store some value into HashMap or Stack or Queue
     * @param command: the commands input by user
     */

    static Data data = new Data();

    /**
     * Get from Data.java
     */
    protected static Map<String, String> labelCMDMap = data.getLabelCMDMap();
    /**
     * Get from Data.java
     */
    protected static Map<String, String> expRefLabelCmd = data.getExpRefLabelCmd();
    /**
     * Get from Data.java
     */
    protected static Map<String, String> breakPointMap = data.getBreakPointMap();
    /**
     * Get from Data.java
     */
    protected static Map<String, List<Object>> varHistoryMap = data.getVarHistoryMap();
    /**
     * Get from Data.java
     */
    protected static Map<String, Object> varMap = data.getVarMap();
    /**
     * Get from Data.java
     */
    protected static Map<String, String> programMap = data.getProgramMap();
    /**
     * Get from Data.java
     */
    protected static Map<String, Object> resultExp = data.getResultExp();
    /**
     * Get from Data.java
     */
    protected static int DebugPoint = data.getDebugPoint();
    /**
     * Get from Data.java
     */
    protected static int index = data.getIndex();
    /**
     * Get from Data.java
     */
    protected static String currentVarValue = data.getCurrentVarValue();
    /**
     * Get from Data.java
     */
    protected static String currentDebugPoint = data.getCurrentDebugPoint();
    /**
     * Get from Data.java
     */
    protected static String currentInspect = data.getCurrentInspect();
    /**
     * Get from Data.java
     */
    protected static Stack<String> stack = data.getStack();
    /**
     * Get from Data.java
     */
    protected static Queue<String> queue = data.getQueue();
    /**
     * Get from Data.java
     */
    protected static List<String> runArray = data.getRunArray();

    public static void storeCommand(String command){

        // Check if instruction is valid first
        String[] splitStr = command.split(" ");  // Split instruction into words


        if (splitStr[0].equals("vardef")){

            labelCMDMap.put(splitStr[1], command);
            classification(command);
        }
        else if (splitStr[0].equals("binexpr") || splitStr[0].equals("unexpr")){

            expRefLabelCmd.put(splitStr[1], command);
            classification(command);
            Simple.updateExp();
        }
        else if (splitStr[0].equals("block")){
            String[] instructions = Arrays.copyOfRange(splitStr, 2, splitStr.length);
            labelCMDMap.put(splitStr[1], command);
        }
        else if (splitStr[0].equals("program") || splitStr[0].equals("execute") || splitStr[0].equals("list") || splitStr[0].equals("store") || splitStr[0].equals("load") || splitStr[0].equals("inspect")){
            classification(command);
        }
        else if (splitStr[0].equals("togglebreakpoint")) {
            try {
                if (breakPointMap.get(splitStr[1]).equals(splitStr[2])) {
                    breakPointMap.remove(splitStr[1]);
                    stack.clear();
                    currentDebugPoint = "";
                }
            } catch (Exception e) {
                breakPointMap.put(splitStr[1], splitStr[2]);
            }
        }
        else {
            labelCMDMap.put(splitStr[1], command);
        }
//        System.out.println(varMap);

    }

    /**
     * Put the variable history into the varHistoryMap this HashMap
     * @param varName: the variable name
     */
    protected static void putVarHistoryMap(String varName) {
        List<Object> list = new ArrayList<>();

        if(!varHistoryMap.containsKey(varName)) {
            varHistoryMap.put(varName, list);
        }
        varHistoryMap.get(varName).add(varMap.get(varName));
//        for (Object e : varHistoryMap.get("x")) {
//            System.out.print(e + " ");
//        }
    }


    /**
     *
     * @param command: the commands input by user
     */
    public static void classification(String command) {

        // Classify the commands

        String[] splitStr = command.split(" ");  // Split instruction into words
        String instruction = splitStr[0];

        
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
                Simple.block(instructions);
                break;
                
            case "if":  //* REQ8
                Simple.ifF(splitStr[2], splitStr[3], splitStr[4]);
                break;

            case "while":   //* REQ9
                Simple.whileW(splitStr[2], splitStr[3]);
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
                Simple.list(programMap.get(splitStr[1]), new ArrayList<String>());
                break;

            case "store":   //* REQ13
                // Generated by vs-code
                try { Simple.store(splitStr[1], splitStr[2]);} 
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;

            case "load":    //* REQ1
                try { Simple.load(splitStr[1],splitStr[2]);} 
                catch (IOException e) {
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

            case "inspect":
                Simple.inspect(splitStr);
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
        if (varMap.containsKey(expression)) return varMap.get(expression);
            
        // If variable is expression reference
        if (resultExp.containsKey(expression)){
            if (resultExp.get(expression) instanceof Integer){return (int) resultExp.get(expression);}
            else return (boolean)resultExp.get(expression);
        }

        try{return Integer.parseInt(expression);}
        catch (Exception ignored){
        }

        return null;

    }

    /**
     * addResultExp function is put the result and label to resultExp this HashMap
     * @param label: the label of expression statement
     * @param object: the result of expression
     */
    public static void addResultExp(String label, Object object){resultExp.put(label, object);}

    /**
     * inputCMD function is let user input those commands
     */
    // Use for input command, but just model, Application will be call this
    public void inputCMD() {

        Scanner inputLine = new Scanner(System.in);

        while(inputLine.hasNextLine()){

            String input = inputLine.nextLine();
            if (input != null){
                if (input.equals("quit")) System.exit(0);


//                if (!inputValidator(input)){
//                    System.out.println("ERROR: The input command is invalid, please enter a new command");
//                    input = inputLine.nextLine();
//                }

                storeCommand(input);
                // classification(input);
            }
            if (input.contains("debug")) {
                classification(input);
            }

        }

        inputLine.close();
    }

    //! Separate store instructions and execute instructions in if while print block cases

    /**
     * inputValidator function is check the user input is legal and the input is right
     * @param input: the input commands by user
     * @return return the right or wrong of user inputs
     */
    public boolean inputValidator(String input){

//        System.out.print("first: ");
//        System.out.println(input);

        // Check if the cmd string is empty
        if (input.isEmpty()){
            return false;
        }

        String[] strTemp = input.split(" ");  // Split instruction into words
        int n = strTemp.length;

//        System.out.print("second: ");
//        System.out.println(strTemp[0]);

        // For checking word length for input string split and if any illegal char are used
        for (int i=0; i<n; i++){
            // Initializing loop variables
            int nrChar = strTemp[i].length();

            // Check if input strings != cmd name or type
            switch (strTemp[0]){
                case "vardef":
                    if(i != 0 && i != 2) {
                        if (strTemp[i].matches("vardef|binexpr|unexpr|print|skip|block|if|while|execute|list|assign|program|store|quit|int|bool")) {
                            System.out.print("third: ");
                            System.out.println("case vardef i != 0");
                            return false;
                        }
                    }
                    break;
                case "binexpr": case "unexpr": case "assign": case "print": case "skip": case "block": case "ifF": case "whileW": case "program": case "execute": case "list": case "store": case "load":
                    if(i != 0) {
                        if (strTemp[i].matches("vardef|binexpr|unexpr|print|skip|block|if|while|execute|list|assign|program|store|quit|int|bool|true|false")) {
                            System.out.print("third: ");
                            System.out.println("case 'the rest' i != 0");
                            return false;
                        }
                    }
                    break;
            }

            // Check length longer than 8 characters & if strTemp[i] is
            if (strTemp[0].equals("store") || strTemp[0].equals("load")){
                switch (strTemp[0]){
                case "store":
                    if (i == 2){
                    }else {
                        if(nrChar > 8){
                            System.out.print("forth: ");
                            System.out.println("nrChar < 8");
                            return false;
                        }
                    }break;
                case "load":
                    if (i == 1){
                    }else {
                        if(nrChar > 8){
                            System.out.print("forth: ");
                            System.out.println("nrChar < 8");
                            return false;
                        }
                    }break;
                case "binexpr": case "unexpr": case "assign": case "print": case "skip": case "block": case "ifF": case "whileW": case "program": case "execute": case "list":
                    if(nrChar > 8){
                        System.out.print("forth: ");
                        System.out.println("nrChar < 8");
                        return false;
                    }break;
                }
            }
//            if(nrChar > 8){
//                System.out.print("forth: ");
//                System.out.println("nrChar < 8");
//                return false;
//            }

            // Loop for checking if any invalid characters are in the String cmd
            switch (strTemp[0]){
                case "store": case "load":
                    if (strTemp[0].equals("store") && i == 2){
                    } else if (strTemp[0].equals("load") && i == 1) {
                    }else {
                        for (int x = 0; x < nrChar; x++) {
                            boolean validChar = Character.isLetterOrDigit(strTemp[i].charAt(x));
                            String temp = String.valueOf(strTemp[i]);
                            if (strTemp[i].matches("-|/|#|~|>|>=|<|<=|==|!=|&&|!|:") || strTemp[i].charAt(x) == '-' || temp.equals("*") || temp.equals("+") || temp.equals("||")) {
                                validChar = true;
                            }
                            if (validChar == false) {
                                System.out.println("The following input is invalid:" + strTemp[i]);
                                System.out.print("fifth: ");
                                System.out.println("validChar == false");
                                return false;
                            }
                        }
                    }break;
                case "vardef": case "binexpr": case "unexpr": case "assign": case "print": case "skip": case "block": case "ifF": case "whileW": case "program": case "execute": case "list":
                    for (int x = 0; x < nrChar; x++) {
                        boolean validChar = Character.isLetterOrDigit(strTemp[i].charAt(x));
                        String temp = String.valueOf(strTemp[i]);
                        if (strTemp[i].matches("-|/|#|~|>|>=|<|<=|==|!=|&&|!|:") || strTemp[i].charAt(x) == '-' || temp.equals("*") || temp.equals("+") || temp.equals("||")) {
                            validChar = true;
                        }
                        if (validChar == false) {
                            System.out.println("The following input is invalid:" + strTemp[i]);
                            System.out.print("fifth: ");
                            System.out.println("validChar == false");
                            return false;
                        }
                    }
            }
        }

        // Checks if the int or bool value is out of bounds
          if(strTemp.length >= 2){
            switch (strTemp[2]) {
                case "int":
                    int intValue = Integer.parseInt(strTemp[4]);
                    int max = Integer.parseInt("99999");
                    int min = Integer.parseInt("-99999");
                    if (min > intValue || intValue > max) {
                        System.out.print("sixth: ");
                        System.out.println("case int != (-99999, 99999)");
                        return false;
                    }
                    break;
                case "bool":
                    String boolValue = strTemp[4];
                    if (!boolValue.matches("false|true")) {
                        System.out.print("sixth: ");
                        System.out.println("case bool != false or true");
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

}
