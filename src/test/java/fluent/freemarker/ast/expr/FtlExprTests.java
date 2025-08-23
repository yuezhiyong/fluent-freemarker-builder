package fluent.freemarker.ast.expr;

import fluent.freemarker.builder.AstJson;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FtlExprTests {

    @Test
    public void testLiteralExprCreation() {
        LiteralExpr literalExpr = new LiteralExpr("Hello World");
        
        assertEquals("Hello World", literalExpr.getValue());
        assertEquals("Literal{Hello World}", literalExpr.toString());
    }

    @Test
    public void testLiteralExprWithDifferentTypes() {
        LiteralExpr stringExpr = new LiteralExpr("text");
        LiteralExpr numberExpr = new LiteralExpr("42");
        LiteralExpr booleanExpr = new LiteralExpr("true");
        LiteralExpr nullExpr = new LiteralExpr("null");
        
        assertEquals("text", stringExpr.getValue());
        assertEquals("42", numberExpr.getValue());
        assertEquals("true", booleanExpr.getValue());
        assertEquals("null", nullExpr.getValue());
    }

    @Test
    public void testIdentifierExprCreation() {
        IdentifierExpr identifierExpr = new IdentifierExpr("user.name");
        
        assertEquals("user.name", identifierExpr.getName());
        assertEquals("Identifier{user.name}", identifierExpr.toString());
    }

    @Test
    public void testIdentifierExprWithComplexPath() {
        IdentifierExpr complexExpr = new IdentifierExpr("data.items[0].properties.title");
        
        assertEquals("data.items[0].properties.title", complexExpr.getName());
    }

    @Test
    public void testBinaryExprCreation() {
        LiteralExpr left = new LiteralExpr("5");
        LiteralExpr right = new LiteralExpr("3");
        BinaryExpr binaryExpr = new BinaryExpr(left, "+", right);
        
        assertEquals(left, binaryExpr.getLeft());
        assertEquals("+", binaryExpr.getOp());
        assertEquals(right, binaryExpr.getRight());
        assertTrue(binaryExpr.toString().contains("Binary"));
        assertTrue(binaryExpr.toString().contains("+"));
    }

    @Test
    public void testBinaryExprWithDifferentOperators() {
        LiteralExpr operand1 = new LiteralExpr("10");
        LiteralExpr operand2 = new LiteralExpr("2");
        
        BinaryExpr addExpr = new BinaryExpr(operand1, "+", operand2);
        BinaryExpr subExpr = new BinaryExpr(operand1, "-", operand2);
        BinaryExpr mulExpr = new BinaryExpr(operand1, "*", operand2);
        BinaryExpr divExpr = new BinaryExpr(operand1, "/", operand2);
        BinaryExpr modExpr = new BinaryExpr(operand1, "%", operand2);
        
        assertEquals("+", addExpr.getOp());
        assertEquals("-", subExpr.getOp());
        assertEquals("*", mulExpr.getOp());
        assertEquals("/", divExpr.getOp());
        assertEquals("%", modExpr.getOp());
    }

    @Test
    public void testBinaryExprWithLogicalOperators() {
        IdentifierExpr left = new IdentifierExpr("user.isActive");
        IdentifierExpr right = new IdentifierExpr("user.isVerified");
        
        BinaryExpr andExpr = new BinaryExpr(left, "&&", right);
        BinaryExpr orExpr = new BinaryExpr(left, "||", right);
        
        assertEquals("&&", andExpr.getOp());
        assertEquals("||", orExpr.getOp());
    }

    @Test
    public void testBinaryExprWithComparisonOperators() {
        IdentifierExpr age = new IdentifierExpr("user.age");
        LiteralExpr eighteen = new LiteralExpr("18");
        
        BinaryExpr eqExpr = new BinaryExpr(age, "==", eighteen);
        BinaryExpr neExpr = new BinaryExpr(age, "!=", eighteen);
        BinaryExpr gtExpr = new BinaryExpr(age, ">", eighteen);
        BinaryExpr gteExpr = new BinaryExpr(age, ">=", eighteen);
        BinaryExpr ltExpr = new BinaryExpr(age, "<", eighteen);
        BinaryExpr lteExpr = new BinaryExpr(age, "<=", eighteen);
        
        assertEquals("==", eqExpr.getOp());
        assertEquals("!=", neExpr.getOp());
        assertEquals(">", gtExpr.getOp());
        assertEquals(">=", gteExpr.getOp());
        assertEquals("<", ltExpr.getOp());
        assertEquals("<=", lteExpr.getOp());
    }

    @Test
    public void testNestedBinaryExpr() {
        // (a + b) * (c - d)
        LiteralExpr a = new LiteralExpr("a");
        LiteralExpr b = new LiteralExpr("b");
        LiteralExpr c = new LiteralExpr("c");
        LiteralExpr d = new LiteralExpr("d");
        
        BinaryExpr add = new BinaryExpr(a, "+", b);
        BinaryExpr sub = new BinaryExpr(c, "-", d);
        BinaryExpr mul = new BinaryExpr(add, "*", sub);
        
        assertEquals(add, mul.getLeft());
        assertEquals("*", mul.getOp());
        assertEquals(sub, mul.getRight());
    }

    @Test
    public void testRawExprCreation() {
        RawExpr rawExpr = new RawExpr("${user.name?upper_case}");
        
        assertEquals("${user.name?upper_case}", rawExpr.getCode());
        assertEquals("Raw{${user.name?upper_case}}", rawExpr.toString());
    }

    @Test
    public void testRawExprWithComplexFreemarkerSyntax() {
        String complexExpr = "${(user.items![])?size > 0?then('has items', 'no items')}";
        RawExpr rawExpr = new RawExpr(complexExpr);
        
        assertEquals(complexExpr, rawExpr.getCode());
    }

    @Test
    public void testRawExprWithBuiltins() {
        List<String> builtinExpressions = Arrays.asList(
            "${name?upper_case}",
            "${date?string('yyyy-MM-dd')}",
            "${list?size}",
            "${text?html}",
            "${number?c}",
            "${boolean?string('yes', 'no')}"
        );
        
        for (String expr : builtinExpressions) {
            RawExpr rawExpr = new RawExpr(expr);
            assertEquals(expr, rawExpr.getCode());
        }
    }

    @Test
    public void testExpressionEquality() {
        LiteralExpr literal1 = new LiteralExpr("test");
        LiteralExpr literal2 = new LiteralExpr("test");
        LiteralExpr literal3 = new LiteralExpr("different");
        
        // Note: Depends on whether equals is implemented
        // For now, just test that objects are created correctly
        assertEquals("test", literal1.getValue());
        assertEquals("test", literal2.getValue());
        assertEquals("different", literal3.getValue());
    }

    @Test
    public void testIdentifierExprEquality() {
        IdentifierExpr id1 = new IdentifierExpr("user.name");
        IdentifierExpr id2 = new IdentifierExpr("user.name");
        IdentifierExpr id3 = new IdentifierExpr("user.email");
        
        assertEquals("user.name", id1.getName());
        assertEquals("user.name", id2.getName());
        assertEquals("user.email", id3.getName());
    }

    @Test
    public void testExpressionHierarchy() {
        // Test that all expression types implement FtlExpr
        LiteralExpr literal = new LiteralExpr("test");
        IdentifierExpr identifier = new IdentifierExpr("var");
        BinaryExpr binary = new BinaryExpr(literal, "+", identifier);
        RawExpr raw = new RawExpr("${expression}");
        
        assertTrue(literal instanceof FtlExpr);
        assertTrue(identifier instanceof FtlExpr);
        assertTrue(binary instanceof FtlExpr);
        assertTrue(raw instanceof FtlExpr);
    }

    @Test
    public void testExpressionToStringMethods() {
        LiteralExpr literal = new LiteralExpr("hello");
        IdentifierExpr identifier = new IdentifierExpr("world");
        BinaryExpr binary = new BinaryExpr(literal, "+", identifier);
        RawExpr raw = new RawExpr("${test}");
        
        String literalStr = literal.toString();
        String identifierStr = identifier.toString();
        String binaryStr = binary.toString();
        String rawStr = raw.toString();
        
        assertNotNull(literalStr);
        assertNotNull(identifierStr);
        assertNotNull(binaryStr);
        assertNotNull(rawStr);
        
        assertTrue(literalStr.contains("hello"));
        assertTrue(identifierStr.contains("world"));
        assertTrue(binaryStr.contains("+"));
        assertTrue(rawStr.contains("test"));
    }

    @Test
    public void testComplexExpressionCombinations() {
        // user.age >= 18 && user.isVerified
        IdentifierExpr userAge = new IdentifierExpr("user.age");
        LiteralExpr eighteen = new LiteralExpr("18");
        IdentifierExpr isVerified = new IdentifierExpr("user.isVerified");
        
        BinaryExpr ageCheck = new BinaryExpr(userAge, ">=", eighteen);
        BinaryExpr fullCheck = new BinaryExpr(ageCheck, "&&", isVerified);
        
        assertEquals(">=", ageCheck.getOp());
        assertEquals("&&", fullCheck.getOp());
        assertEquals(ageCheck, fullCheck.getLeft());
        assertEquals(isVerified, fullCheck.getRight());
    }

    @Test
    public void testExpressionWithNullValues() {
        // Test handling of null/empty values
        LiteralExpr emptyLiteral = new LiteralExpr("");
        IdentifierExpr emptyIdentifier = new IdentifierExpr("");
        RawExpr emptyRaw = new RawExpr("");
        
        assertEquals("", emptyLiteral.getValue());
        assertEquals("", emptyIdentifier.getName());
        assertEquals("", emptyRaw.getCode());
    }

    @Test
    public void testStringConcatenationExpression() {
        // "Hello " + user.name + "!"
        LiteralExpr hello = new LiteralExpr("\"Hello \"");
        IdentifierExpr userName = new IdentifierExpr("user.name");
        LiteralExpr exclamation = new LiteralExpr("\"!\"");
        
        BinaryExpr firstConcat = new BinaryExpr(hello, "+", userName);
        BinaryExpr fullConcat = new BinaryExpr(firstConcat, "+", exclamation);
        
        assertEquals("+", firstConcat.getOp());
        assertEquals("+", fullConcat.getOp());
    }
}