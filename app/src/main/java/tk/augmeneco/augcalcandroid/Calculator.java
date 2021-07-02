package tk.augmeneco.augcalcandroid;

import java.util.HashMap;
import java.util.Map;

public class Calculator {
    static double calculate(MathAST ast) {
        return calculate(ast, new HashMap<>());
    }

    static double calculate(MathAST ast, Map<String, Double> args) {
        if (ast instanceof Constant)
            return ((Constant) ast).value;
        else if (ast instanceof Pi)
            return Math.PI;
        else if (ast instanceof E)
            return Math.E;
        else if (ast instanceof Sin)
            return Math.sin(calculate(((Sin) ast).a, args));
        else if (ast instanceof Cos)
            return Math.cos(calculate(((Cos) ast).a, args));
        else if (ast instanceof Tan)
            return Math.tan(calculate(((Tan) ast).a, args));
        else if (ast instanceof Ctg)
            return 1.0 / Math.tan(calculate(((Ctg) ast).a, args));
        else if (ast instanceof Lg)
            return Math.log10(calculate(((Lg) ast).a, args));
        else if (ast instanceof Ln)
            return Math.log(calculate(((Ln) ast).a, args));
        else if (ast instanceof Usub)
            return -calculate(((Usub) ast).a, args);
        else if (ast instanceof Asin)
            return Math.asin(calculate(((Asin) ast).a, args));
        else if (ast instanceof Acos)
            return Math.acos(calculate(((Acos) ast).a, args));
        else if (ast instanceof Atg)
            return Math.atan(calculate(((Atg) ast).a, args));
        else if (ast instanceof Actg)
            return Math.atan(1.0 / calculate(((Actg) ast).a, args));
        else if (ast instanceof Sqrt)
            return Math.sqrt(calculate(((Sqrt) ast).a, args));

        else if (ast instanceof Add)
            return calculate(((Add) ast).a, args) + calculate(((Add) ast).b, args);
        else if (ast instanceof Sub)
            return calculate(((Sub) ast).a, args) - calculate(((Sub) ast).b, args);
        else if (ast instanceof Mul)
            return calculate(((Mul) ast).a, args) * calculate(((Mul) ast).b, args);
        else if (ast instanceof Div)
            return calculate(((Div) ast).a, args) / calculate(((Div) ast).b, args);
        else if (ast instanceof Pow)
            return Math.pow(calculate(((Pow) ast).a, args), calculate(((Pow) ast).b, args));

        else if (ast instanceof Variable)
            if (args.containsKey(((Variable) ast).name))
                return args.get(((Variable) ast).name);
            else
                throw new RuntimeException("Calculate error: no value provided for variable: "+((Variable) ast).name);
        else
            throw new RuntimeException("Calculate error: unknown MathAST node type");
    }
}
