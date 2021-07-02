package tk.augmeneco.augcalcandroid;

import java.util.ArrayList;
import java.util.List;

class MathAST { }

class LeafToken extends MathAST { }
class SingleToken extends MathAST {
    MathAST a;
    SingleToken with(MathAST a) {
        this.a = a;
        return this;
    }
}
class DoubleToken extends MathAST {
    MathAST a;
    MathAST b;
    DoubleToken with(MathAST a, MathAST b) {
        this.a = a;
        this.b = b;
        return this;
    }
}

class Pi extends LeafToken { }
class E extends LeafToken { }

class Sin extends SingleToken { }
class Cos extends SingleToken { }
class Tan extends SingleToken { }
class Ctg extends SingleToken { }
class Lg extends SingleToken { }
class Ln extends SingleToken { }
class Abs  extends SingleToken { }
class Usub extends SingleToken { }
class Asin extends SingleToken { }
class Acos extends SingleToken { }
class Atg extends SingleToken { }
class Actg extends SingleToken { }
class Sqrt extends SingleToken { }


class Mul extends DoubleToken {   }
class Add extends DoubleToken {   }
class Sub extends DoubleToken {   }
class Div extends DoubleToken {   }
class Pow extends DoubleToken {   }


class Variable extends LeafToken {
    String name;
    Variable with(String name) {
        this.name = name;
        return this;
    }
}

class Constant extends LeafToken {
    double value;
    Constant with(double value) {
        this.value = value;
        return this;
    }
}


class Differentiate extends LeafToken {
    MathAST f;
    String dx;
    Differentiate with(MathAST f, String dx) {
        Differentiate o = new Differentiate();
        o.f = f;
        o.dx = dx;
        return o;
    }
}

class Lists {
    static List<Class<? extends LeafToken>> constants = new ArrayList<Class<? extends LeafToken>>() {{
        add(Pi.class);
        add(E.class);
    }};

    static List<Class<? extends SingleToken>> functions = new ArrayList<Class<? extends SingleToken>>() {{
        add(Sin.class);
        add(Cos.class);
        add(Tan.class);
        add(Ctg.class);
        add(Lg.class);
        add(Ln.class);
        add(Abs.class);
        add(Usub.class);
        add(Asin.class);
        add(Acos.class);
        add(Atg.class);
        add(Actg.class);
        add(Sqrt.class);
    }};
}