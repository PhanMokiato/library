package com.company;

import java.util.Stack;
import static java.lang.Math.pow;

public class Expression {
    private static boolean isOperator(char op) {
        return op == '+' || op == '-' || op == '*' || op == '/' || op == '^';
    }
    private static boolean isDigit(char digit) {
        return digit == '1' || digit == '2' ||digit == '3' || digit == '4'
                || digit == '5' || digit == '6' || digit == '7' || digit == '8'
                || digit == '9' || digit == '0';
    }
    private static boolean checkDigits(char[] ex, int n) {
        return isDigit(ex[n - 1]) && isDigit(ex[n + 1]);
    }
    private static boolean checkDigitsAndBrackets1(char[] ex, int n) {
        return isDigit(ex[n - 1]) && ex[n + 1] == '(';
    }
    private static boolean checkDigitsAndBrackets2(char[] ex, int n) {
        return isDigit(ex[n + 1]) && ex[n - 1] == ')';
    }
    private static boolean checkBrackets(char[] ex, int n) {
        return ex[n - 1] == ')' && ex[n + 1] == '(';
    }
    private static boolean checkNegativeInBrackets(char[] ex, int n) {
        return ex[n - 1] == '(' && isDigit(ex[n + 1]);
    }
    public static boolean checkForMistakes(String expression) {
        char[] exp = expression.toCharArray();
        boolean ok = true;
        int brackets1 = 0;
        int brackets2 = 0;
        int checked = 0;
        int operators = 0;
        int k = exp.length;
        int min = 0;
        for (char c : exp) {
            if (!isDigit(c) && !isOperator(c) && c != '.' && c != '(' && c != ')') {
                return false;
            }
        }
        if (isOperator(exp[k - 1]) || exp[k - 1] == '(' || exp[k - 1] == '.') {
            return false;
        }
        if ((isOperator(exp[0]) || exp[0] == '.') && exp[0] != '-') {
            return false;
        }
        if (exp[0] == '-') {++min;}
        for (int i = min; i < k; ++i) {
            if(isOperator(exp[i])) {
                if (!checkDigits(exp, i) && !checkDigitsAndBrackets1(exp, i) &&
                        !checkDigitsAndBrackets2(exp, i) && !checkBrackets(exp, i) &&
                        !(checkNegativeInBrackets(exp, i) && exp[i] == '-')) {
                    ok = false;
                }
            }
            if (exp[i] == '.') {
                if (!isDigit(exp[i - 1]) || !isDigit(exp[i + 1])) {
                    ok = false;
                }
            }
            if (exp[i] == '(') {
                ++brackets1;
                if (exp[i + 1] == ')') {
                    ok = false;
                }
            }
            if (exp[i] == ')') {
                if (brackets1 > 0) {
                    for (int f = i; exp[f] != '(' || (checked != brackets2); --f) {
                        if (isOperator(exp[f])) {
                            ++operators;
                        }
                        if(exp[f] == '(') {
                            ++checked;
                        }
                    }
                    ++brackets2;
                    checked = 0;
                    if (operators == 0) {
                        ok = false;
                    }
                    operators = 0;
                } else {
                    ok = false;
                }
            }
        }
        if (brackets1 != brackets2) {
            ok = false;
        }
        return ok;
    }
    private static int giveOpPriority(char op) {
        String[] optype = { "(", ")", "-+", "*/", "^"};
        int num = -1;
        for (int i = 0; i < 5; ++i) {
            char[] element = optype[i].toCharArray();
            for (char c : element) {
                if (op == c) {
                    num = i;
                    break;
                }
            }
        }
        return num;
    }
    private static String doConversionWithDA(String expression) {
        Stack<String> operators = new Stack<>();
        StringBuilder revPol = new StringBuilder(expression.length());
        StringBuilder number = new StringBuilder();
        boolean minus = false;
        boolean minusb = false;
        char[] exp = expression.toCharArray();
        if (exp[0] == '-') {
            minus = true;
        }
        for (int i = 0; i < exp.length; ++i) {
            String k = new String(exp, i, 1);
            StringBuilder k0 = new StringBuilder(k);
            if (isDigit(exp[i])) {
                number.append(k0);
            }
            if (exp[i] == '.') {
                number.append(k0);
                continue;
            }
            if (!isDigit(exp[i])) {
                if (minus && number.length() == 0) {
                    number.append("(");
                    number.append(k0);
                    continue;
                }
                if (number.length() != 0 && !minus) {
                    revPol.append(number);
                    revPol.append("|");
                    number.setLength(0);
                }
                if (number.length() != 0 && minus) {
                    number.append(")");
                    revPol.append(number);
                    revPol.append("|");
                    number.setLength(0);
                    minus = false;
                    if (exp[i] != ')') {
                        minusb = false;
                    }
                }
                StringBuilder k1 = new StringBuilder("");
                if (!operators.empty()) {
                    k1.append(operators.peek());
                }
                if (giveOpPriority(k0.charAt(0)) == 0) {
                    if (exp[i + 1] == '-') {
                        minus = true;
                        minusb = true;
                    }
                    operators.push(k0.toString());
                    continue;
                }
                if ((operators.empty() || giveOpPriority(k0.charAt(0)) > giveOpPriority(k1.charAt(0))) && giveOpPriority(k0.charAt(0)) != 1){
                    operators.push(k0.toString());
                    continue;
                }
                if ((giveOpPriority(k0.charAt(0)) == giveOpPriority(k1.charAt(0)) || giveOpPriority(k0.charAt(0)) < giveOpPriority(k1.charAt(0)))
                        && giveOpPriority(k0.charAt(0)) != 1) {
                    Stack<String> leftovers = new Stack<>();
                    for (int l = operators.size(); l > 0 && !operators.peek().equals("("); --l) {
                        StringBuilder k2 = new StringBuilder(operators.peek());
                        if (giveOpPriority(k0.charAt(0)) == giveOpPriority(k2.charAt(0))
                                || giveOpPriority(k0.charAt(0)) < giveOpPriority(k2.charAt(0))) {
                            String op = operators.pop();
                            revPol.append(op);
                        } else {
                            String op = operators.pop();
                            leftovers.push(op);
                        }
                    }
                    int n1 = leftovers.size();
                    for (int n2 = 0; n2 < n1; ++n2) {
                        String k3 = leftovers.pop();
                        operators.push(k3);
                    }
                    operators.push(k0.toString());
                    continue;
                }
                if (giveOpPriority(k0.charAt(0)) == 1) {
                    if (minusb) {
                        minusb = false;
                        continue;
                    }
                    int size = operators.size();
                    for (int s2 = size; !operators.peek().equals("("); --s2) {
                        String k4 = operators.pop();
                        revPol.append(k4);
                    }
                    operators.pop();
                }
            }
        }
        if (number.length() != 0) {
            revPol.append(number);
            revPol.append("|");
            number.setLength(0);
        }
        if (!operators.empty()) {
            int f1 = operators.size();
            for (int f2 = f1; f2 > 0; --f2) {
                String k5 = operators.pop();
                if (!k5.equals("(")) {
                    revPol.append(k5);
                }
            }
        }
        return revPol.toString();
    }
    private static double doOperation(double n1, double n2, char op) {
        if (op == '+'){
            return n1 + n2;
        }
        if (op == '-'){
            return n2 - n1;
        }
        if (op == '*'){
            return n1 * n2;
        }
        if (op == '/'){
            return n2 / n1;
        }
        if (op == '^'){
            return pow(n2, n1);
        }
        return 0;
    }
    public static double solveExpression(String expr) {
        String expression = doConversionWithDA(expr);
        char[] exp = expression.toCharArray();
        StringBuilder number = new StringBuilder();
        Stack<Double> numbs = new Stack<>();
        boolean minus = false;
        for (int i = 0; i < exp.length; ++i) {
            String elem = new String(exp, i, 1);
            if (exp[i] == '(') {
                minus = true;
            }
            if (exp[i] == '-' && minus) {
                number.append(elem);
                continue;
            }
            if (exp[i] == ')') {
                minus = false;
            }
            if (isDigit(exp[i])) {
                number.append(elem);
            }
            if (exp[i] == '.') {
                number.append(elem);
                continue;
            }
            if (exp[i] == '|') {
                Double k = Double.valueOf(number.toString());
                numbs.push(k);
                number.setLength(0);
            }
            if (isOperator(exp[i])) {
                double num1 = numbs.pop();
                double num2 = numbs.pop();
                double newNum = doOperation(num1, num2, exp[i]);
                numbs.push(newNum);
            }
        }
        return numbs.pop();
    }
}
