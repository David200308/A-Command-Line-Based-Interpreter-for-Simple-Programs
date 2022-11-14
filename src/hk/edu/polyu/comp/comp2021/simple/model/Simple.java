package hk.edu.polyu.comp.comp2021.simple.model;

import java.util.*;

public class Simple extends Parser {
    final static int maxInt = 99999;
    final static int minInt = -99999;

    public Simple(){
        Parser parser = new Parser();
    }
    
    protected static void vardef(String[] str) {   //* REQ1 - Works
        varMap.put(str[3], expRef(str[4]));
        putVarHistoryMap(str[3]);
        updateExp();
    }


    protected static void binExpr(String[] str) { //* REQ2

        Object a = expRef(str[2]);
        Object b = expRef(str[4]);
        String operator = str[3];
        String label = str[1];

        // If integer
        if (a instanceof Integer && b instanceof Integer){evaluateIntExp((int)a, (int)b, operator, label);}

        // If boolean
        else if (a instanceof Boolean && b instanceof Boolean){evaluateBoolExp((boolean)a, (boolean)b, operator, label);}

    }

    protected static void evaluateIntExp(int a, int b, String operator, String label){   //* Req 2.1

        // Evaluate int expressions and adds int result

        switch (operator){
            case "+":
                Parser.addResultExp(label, a + b);
                break;
            case "-":
                addResultExp(label, a - b);
                break;
            case "*":
                addResultExp(label, a * b);
                break;
            case "/":
                if (b != 0) addResultExp(label, a / b);
                break;
            case ">":
                addResultExp(label, a > b);
                break;
            case "<":
                addResultExp(label, a < b);
                break;
            case ">=":
                addResultExp(label, a >= b);
                break;
            case "<=":
                addResultExp(label, a <= b);
                break;
            case "%":
                addResultExp(label, a % b);
                break;
            case "==":
                addResultExp(label, a == b);
                break;
            case "!=":
                addResultExp(label, a != b);
                break;
        }

    }

    protected static void evaluateBoolExp(Boolean a, Boolean b, String operator, String label){ //* Req 2.2

        // Evaluates Boolean expression. 

        switch (operator){
            case "&&":
                addResultExp(label, a && b);
                break;
            case "||":
                addResultExp(label, a || b);
                break;
            case "==":
                addResultExp(label, a == b);
                break;
            case "!=":
                addResultExp(label, a != b);
                break;
        }
    }

    protected static void unexpr(String expName, String operator, String expRef1) { //* REQ3
        

        if (operator.equals("!")){   // Negates Boolean expression
            if ((boolean)expRef(expRef1)) {
                varMap.put(expName, false);
                putVarHistoryMap(expName);
            }
            else {
                addResultExp(expName, true);
            }
        }

        else if (operator.equals("~")){  // Negates Int Expression by switching symbols
            
            int number = (int)expRef(expRef1) * -1;
            if (operator.equals("~"))
                addResultExp(expName, number);
        }
    }


    protected static void assign(String varName, String expRef) {  //* REQ4

        // Get object in variable
        Object toAdd = expRef(expRef);
        
        // Change variable value
        varMap.replace(varName, toAdd);
        putVarHistoryMap(varName);
        updateExp();

    }

    protected static void updateExp(){
        // Update stored statements that contain this variable
        String[] command;
        for (String key: resultExp.keySet()){
            
            command = null;
            if (expRefLabelCmd.containsKey(key)){
                
                command = expRefLabelCmd.get(key).split(" ");

                if (command[0].equals("unexpr")) unexpr(command[1], command[2], command[3]);
                else if (command[0].equals("binexpr")) binExpr(command);
            }
        }
    }

    protected static void print(String label, String expRef) {   //* REQ5
        String value = "";
        value = value + expRef(expRef).toString();
        System.out.print('[' + value +']');
        addResultExp(label, '[' + value +']');
    }

    protected static void skip(){}   //* REQ6

    protected static void block(String[] instructions){  //* REQ7

        int n = instructions.length;
        for (int i = 0; i < n; i++){

            updateExp();
            if (labelCMDMap.containsKey(instructions[i]))
                classification(labelCMDMap.get(instructions[i]));
            else if (expRefLabelCmd.containsKey(instructions[i]))
                classification(labelCMDMap.get(instructions[i]));
        }

    }

    protected static void ifF(String expRef, String statementLab1, String statementLab2){    //* REQ

        // Check if the condition is TRUE or FALSE:
        // if true - <K,V> save V for label ex1 in labelCMDMap
        // if false - <K,V> save V for label ex2 in labelCMDMap

        if ((boolean)expRef(expRef)){
            classification(labelCMDMap.get(statementLab1));
        }else {
            classification(labelCMDMap.get(statementLab2));
        }

        updateExp();

    }

    protected static void whileW(String expRef, String statementLab1) {   //* REQ9

        

        while((boolean)expRef(expRef)) {
            // Check if the condition is TRUE or FALSE:
            // if true - <K,V> save V for label ex1 in labelCMDMap and
            // run the cmd
            // if false - terminate the loop
            classification(labelCMDMap.get(statementLab1));
            updateExp();
        }
    }
    protected static void program(String programName, String statementLabel) {  //* REQ10
        //ArrayList<String> statementLabelList = new ArrayList<>();
        //statementLabelList.add(statementLabel);
        programMap.put(programName, statementLabel);
    }

    protected static void execute(String programName){  //* REQ11
        classification(Parser.labelCMDMap.get(Parser.programMap.get(programName)));
    }

    protected static void list(String instruction) {    //* REQ12

        // Get instruction from the map

        String[] fullInst = labelCMDMap.get(instruction).split(" ");
        if (blockMap.containsKey(instruction)){  // If program statement is a block
            String block[] = blockMap.get(fullInst[1]);
            System.out.println(labelCMDMap.get(instruction));
            for (int i = 0; i < block.length; i++) {
                list(block[i]); // Recurse over the instructions
            }
        }
        else if (fullInst[0].equals("while")){    // If while loop
            System.out.println(labelCMDMap.get(instruction));
            list(fullInst[3]);
        }
        else if (fullInst[0].equals("if")){
            System.out.println(labelCMDMap.get(instruction));
            list(fullInst[3]);
            list(fullInst[4]);
        }
    
        // Print if instruction is not a while or block or if
        else{
            Parser.queue.add(labelCMDMap.get(instruction));
            System.out.println(labelCMDMap.get(instruction));
        }

        
    }

    protected static void togglebreakpoint(String programName, String label) {

    }



    protected static void debug(String programName) {
        storeQueue(programMap.get(programName));
        if (DebugPoint < 1) {
            currentDebugPoint = breakPointMap.get(programName);
            currentInspect = currentDebugPoint;
            DebugPoint++;
        }
        try {
            while (true) {
//                if (stack.peek().equals("while")) {
//                    while (true) {
//                        String peekCMD = stack.peek();
//                        System.out.println("Debugging ==> " + peekCMD);
//                        stack.pop();
//                        currentInspect = currentDebugPoint;
//                        currentDebugPoint = stack.peek().split(" ")[1];
//                        break;
//                    }
//                }
                if (!stack.peek().split(" ")[1].equals(currentDebugPoint) || currentDebugPoint.equals("")) {
                    stack.pop();
                } else {
                    String peekCMD = stack.peek();
                    System.out.println("Debugging ==> " + peekCMD);
                    stack.pop();
                    currentInspect = currentDebugPoint;
                    currentDebugPoint = stack.peek().split(" ")[1];
                    break;
                }
            }
        } catch (Exception e) {
            System.out.print("Finish Debug!");
            System.out.println();
        }
    }

    protected static void inspect(String programName, String varName) {
        System.out.println("<" + varHistoryMap.get(varName).get(index) + ">");
        index++;

//        storeQueue(programMap.get(programName));
//        int sizeCount = 0;
//        for (int i = cmdMap.size(); i >= 1; i--) {
//            if (cmdMap.get(i).contains("vardef") || cmdMap.get(i).contains("binexpr") || cmdMap.get(i).contains("unexpr")) {
//                runQueue.add(cmdMap.get(i));
//                sizeCount = sizeCount + 1;
//            }
//        }
//
//        int index = 0;
//        while (true) {
//            if (currentInspect.equals(runQueue.peek())) {
//                break;
//            } else {
//                if (runQueue.peek().contains(varName)) {
//                    index = index + 1;
//                }
//                runQueue.remove();
//            }
//
//        }


//        int sizeCount = 0;
//        for (int i = cmdMap.size(); i >= 1; i--) {
//            if (cmdMap.get(i).contains("vardef") || cmdMap.get(i).contains("binexpr") || cmdMap.get(i).contains("unexpr")) {
//                runQueue.add(cmdMap.get(i));
//                sizeCount = sizeCount + 1;
//            }
//        }
//        for (int i = 0; i <= runQueue.size() + 1 + sizeCount; i++) {
//            if (currentInspect.equals(runQueue.peek())) {
//                runStack.add(runQueue.peek());
//                runQueue.remove();
//                break;
//            }
//        }
//
//        for (int i = 1; i < runStack.size(); i++) {
//            classification(runStack.peek());
//            runStack.remove(runStack.peek());
//        }


//
//
//        try {
//            for (int i = 0; i <= runQueue.size() + 1 + sizeCount; i++) {
//                System.out.println(currentInspect);
//                storeCommand(currentInspect);
//                if (Parser.breakPointMap.containsKey(programName)) {
//
//                }
//                runQueue.remove();
//            }
//
//        } catch (Exception e) {
//            System.out.println("Warning: No Debug first");
//        }
    }

//    protected static void instrument(String programName, String statement, String pos, String expRef){
////        if (pos.equals("after")) {
////            if (Parser.programMap.get(programName)) {
////
////            }
////        }
////
////        if (pos.equals("before")) {
////
////        }
//
//        // print expRef
//        try{
//            if (Parser.varMap.containsKey(expRef)) {
//                System.out.println("{" + Parser.varMap.get(expRef) + "}");
//            }
//        } catch (Exception e) {
//            System.out.println("{" + expRef + "}");
//        }
//
//    }

}

