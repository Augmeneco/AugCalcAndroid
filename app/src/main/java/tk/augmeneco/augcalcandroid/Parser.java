package tk.augmeneco.augcalcandroid;

import android.os.Build;
import android.util.Pair;
import androidx.annotation.RequiresApi;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static tk.augmeneco.augcalcandroid.Lists.constants;
import static tk.augmeneco.augcalcandroid.Lists.functions;

enum Token {
    NAMED_FUNCTION,
    CONSTANT,
    OPERATOR,
    UNARY_OPERATOR,
    LEFT_PARENTHESIS,
    RIGHT_PARENTHESIS,
    NUMBER,
    VARIABLE,
}

class Rule {
    Pattern pattern;
    Token type;
    int args;
    int precedence;
    boolean isLeftAssociative;

    Rule(String pattern, Token type, int args, int precedence, boolean isLeftAssociative) {
        this.pattern = Pattern.compile(pattern);
        this.type = type;
        this.args = args;
        this.precedence = precedence;
        this.isLeftAssociative = isLeftAssociative;
    }

    Rule(String key, Token type) {
        this(key, type, 0, 0, false);
    }
}

public class Parser {
    static ArrayList<Rule> rules = new ArrayList<Rule>(){{
        add(new Rule("[\\~]", Token.OPERATOR, 1, 3, false));
        add(new Rule(functions.stream().map(x->x.getSimpleName().toLowerCase()).collect(Collectors.joining("|")),
                     Token.NAMED_FUNCTION, 1, 4, true));
        add(new Rule(constants.stream().map(x->x.getSimpleName().toLowerCase()).collect(Collectors.joining("|")),
                     Token.CONSTANT));
        add(new Rule("[\\^]", Token.OPERATOR, 2, 3, true));
        add(new Rule("[*\\/]", Token.OPERATOR, 2, 2, true));
        add(new Rule("[+-]", Token.OPERATOR, 2, 1, true));
        add(new Rule("[(\\[]", Token.LEFT_PARENTHESIS));
        add(new Rule("[)\\]]", Token.RIGHT_PARENTHESIS));
        add(new Rule("[0-9.,]+", Token.NUMBER));
        add(new Rule("[a-zA-Z]", Token.VARIABLE));
    }};

    static ArrayList<Pair<String, Rule>> tokenize(String s) {
        s = s.replaceAll("(^|[\\(\\+\\-\\*/\\^])\\-", "$1~");
        s = s.replaceAll("(^|[\\(\\+\\-\\*/\\^])\\+", "$1");

        ArrayList<Pair<String, Rule>> out = new ArrayList<>();
        int start = 0;
        while(start < s.length()) {
            Iterator<Rule> i;
            for(i = rules.iterator(); i.hasNext(); ) {
                Rule rule = i.next();

                Matcher matcher = rule.pattern.matcher(s.substring(start));
                if(matcher.lookingAt()) {
                    out.add(new Pair<String, Rule>(matcher.group(0), rule));
                    start = start+matcher.end(0);
                    break;
                }
            }
            if(!i.hasNext()) {
                throw new RuntimeException(String.format("Tokenization error near %d: %s", start, s.substring(start)));
            }
        }
        return out;
    }

    static MathAST shuntingYardAST(ArrayList<Pair<String, Rule>> tokens) {
        Stack<Pair<String, Rule>> op_stack = new Stack<>();
        Stack<MathAST> out = new Stack<>();

        Consumer<String> addOp = op -> {
            // ???????????????? ???? ?????????????? ??????????????????
            MathAST b = out.pop();
            if (op.charAt(0) == '~') {
                out.push(new Usub().with(b));
                return;
            }
            // ???????????????? ???? ???????????????? ??????????????????
            MathAST a = out.pop();
            if(op.charAt(0) == '+')
                out.push(new Add().with(a,b));
            else if(op.charAt(0) == '-')
                out.push(new Sub().with(a,b));
            else if(op.charAt(0) == '*')
                out.push(new Mul().with(a,b));
            else if(op.charAt(0) == '/')
                out.push(new Div().with(a,b));
            else if(op.charAt(0) == '^')
                out.push(new Pow().with(a,b));
        };

        Set<Token> leaf = new HashSet<Token>(){{add(Token.CONSTANT); add(Token.NUMBER); add(Token.VARIABLE);}};
        for(Pair<String, Rule> token : tokens) {
            if (token.second.type == Token.CONSTANT)
                for (Class<? extends LeafToken> x : constants) {
                    if (token.first.equals(x.getSimpleName().toLowerCase())) {
                        try {
                            out.push(x.newInstance());
                        } catch (Exception e) {
                            throw new RuntimeException("ShunYard error: can't create constant object");
                        }
                        break;
                    }
                }
            else if (token.second.type == Token.NUMBER) {
                try {
                    out.push(new Constant().with(Double.parseDouble(token.first)));
                } catch (Exception e) {
                    throw new RuntimeException("ShunYard error: can't parse number: \""+token.first+"\"");
                }
            }
            else if (token.second.type == Token.VARIABLE)
                out.push(new Variable().with(token.first));
            else if (token.second.type == Token.NAMED_FUNCTION)
                op_stack.push(token);
            else if (token.second.type == Token.OPERATOR) {
                while ((op_stack.size() > 0)
                        && (op_stack.elementAt(op_stack.size() - 1).second.type != Token.LEFT_PARENTHESIS)
                        && ((op_stack.elementAt(op_stack.size() - 1).second.precedence > token.second.precedence)
                        || (op_stack.elementAt(op_stack.size() - 1).second.precedence == token.second.precedence && token.second.isLeftAssociative)))
                    addOp.accept(op_stack.pop().first);
                op_stack.push(token);
            }
            else if (token.second.type == Token.LEFT_PARENTHESIS)
                op_stack.push(token);
            else if (token.second.type == Token.RIGHT_PARENTHESIS) {
                while ((op_stack.elementAt(op_stack.size() - 1).second.type != Token.LEFT_PARENTHESIS) || (op_stack.size() == 0))
                    addOp.accept(op_stack.pop().first);
                // If the stack runs out without finding a left parenthesis, then there are mismatched parentheses.
                if (op_stack.size() == 0)
                    throw new RuntimeException("ShunYard error: no right parenthesis");
                if (op_stack.elementAt(op_stack.size() - 1).second.type == Token.LEFT_PARENTHESIS) // ?????????? ?????
                    op_stack.pop();
                if ((op_stack.size() > 0) && (op_stack.elementAt(op_stack.size() - 1).second.type == Token.NAMED_FUNCTION)) {
                    Pair<String, Rule> fun = op_stack.pop();
                    MathAST a = out.pop();
                    for (Class<? extends SingleToken> x : functions)
                        if (fun.first.equals(x.getSimpleName().toLowerCase())) {
                            try {
                                out.push((x.newInstance()).with(a));
                            } catch (Exception e) {
                                throw new RuntimeException("ShunYard error: can't create function object");
                            }
                            break;
                        }
                }
            }
        }
        while (op_stack.size() > 0)
            addOp.accept(op_stack.pop().first);

        MathAST result = out.pop();
        if (out.size() > 0)
            throw new RuntimeException("ShunYard: output stack is not empty");
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    static MathAST parse(String expr) {
        String prepare = expr.replaceAll(" ", "");
        ArrayList<Pair<String, Rule>> tokens = tokenize(prepare);
        return shuntingYardAST(tokens);
    }
}
