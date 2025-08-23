package fluent.freemarker;

import fluent.freemarker.ast.*;
import fluent.freemarker.ast.expr.LiteralExpr;
import fluent.freemarker.builder.FtlBuilder;
import fluent.freemarker.variable.FluentFreemarkerContext;
import fluent.freemarker.variable.ValidationRecorder;
import fluent.freemarker.variable.VariableReference;
import lombok.Data;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FtlBuilderTests {
    private FluentFreemarkerContext context;

    @BeforeEach
    void setUp() {
        context = FluentFreemarkerContext.create()
                .var("user", new TestUser("John", 25))
                .var("items", Arrays.asList("item1", "item2"))
                .var("orders", Collections.singletonList(new TestOrder("ORD001", 100.0)))
                .at("test.ftl");
    }

    // 测试用的简单类
    @Data
    public static class TestUser {
        private final String name;
        private final int age;

        public TestUser(String name, int age) {
            this.name = name;
            this.age = age;
        }

    }

    @Getter
    public static class TestOrder {
        private final String id;
        private final double amount;

        public TestOrder(String id, double amount) {
            this.id = id;
            this.amount = amount;
        }


    }

    @Test
    void testCreateBuilder() {
        FtlBuilder builder = FtlBuilder.create(context);
        assertNotNull(builder);
        assertNotNull(builder.getContext());
        assertNotNull(builder.getValidationRecorder());
    }

    @Test
    void testText() {
        FtlBuilder builder = FtlBuilder.create(context);
        builder.text("Hello World");

        List<FtlNode> nodes = builder.build();
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof TextNode);
        assertEquals("Hello World", ((TextNode) nodes.get(0)).getText());
    }

    @Test
    void testVar() {
        FtlBuilder builder = FtlBuilder.create(context);
        builder.var("user.name");

        List<FtlNode> nodes = builder.build();
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof VarNode);
        assertEquals("user.name", ((VarNode) nodes.get(0)).getExpression());

        // 验证变量引用被记录
        ValidationRecorder recorder = builder.getValidationRecorder();
        List<VariableReference> refs = recorder.getReferences();
        assertEquals(1, refs.size());
        assertEquals("user.name", refs.get(0).getExpression());
    }

    @Test
    void testAssign() {
        FtlBuilder builder = FtlBuilder.create(context);
        builder.assign("count", "10");

        List<FtlNode> nodes = builder.build();
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof AssignNode);
        AssignNode assignNode = (AssignNode) nodes.get(0);
        assertEquals("count", assignNode.getVarName());
        assertEquals("10", assignNode.getValueExpr());

        // 验证变量赋值被记录
        ValidationRecorder recorder = builder.getValidationRecorder();
        assertTrue(recorder.isAssigned("count"));
    }

    @Test
    void testIfBlock() {
        FtlBuilder builder = FtlBuilder.create(context);
        builder.ifBlock("user.age > 18", ifBuilder ->
                ifBuilder.text("Adult")
        );

        List<FtlNode> nodes = builder.build();
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof IfNode);
        IfNode ifNode = (IfNode) nodes.get(0);
        assertEquals("user.age > 18", ifNode.getCondition());
        assertEquals(1, ifNode.getThenBlock().size());
        assertTrue(ifNode.getThenBlock().get(0) instanceof TextNode);
    }

    @Test
    void testIfElseBlock() {
        FtlBuilder builder = FtlBuilder.create(context);
        builder.ifElseBlock("user.age > 18",
                ifBuilder -> ifBuilder.text("Adult"),
                elseBuilder -> elseBuilder.text("Minor")
        );
        List<FtlNode> nodes = builder.build();
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof IfNode);
        IfNode ifNode = (IfNode) nodes.get(0);
        assertEquals(1, ifNode.getThenBlock().size());
        assertEquals(1, ifNode.getElseBlock().size());
    }

    @Test
    void testList() {
        FtlBuilder builder = FtlBuilder.create(context);
        builder.list("item", "items", listBuilder ->
                listBuilder.text("- ").var("item").text("\n")
        );

        List<FtlNode> nodes = builder.build();
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof ListNode);
        ListNode listNode = (ListNode) nodes.get(0);
        assertEquals("item", listNode.getItem());
        assertEquals("items", listNode.getListExpression());
        assertEquals(3, listNode.getBody().size());
    }

    @Test
    void testMacro() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "");
        params.put("age", "");

        FtlBuilder builder = FtlBuilder.create(context);
        builder.macro("greet", params, macroBuilder ->
                macroBuilder.text("Hello ").var("name").text(", age ").var("age")
        );

        List<FtlNode> nodes = builder.build();
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof MacroNode);
        MacroNode macroNode = (MacroNode) nodes.get(0);
        assertEquals("greet", macroNode.getName());
        assertEquals(2, macroNode.getParams().size());
        assertEquals(4, macroNode.getBody().size());
    }

    @Test
    void testLocalAndGlobal() {
        FtlBuilder builder = FtlBuilder.create(context);
        builder.local("temp", new LiteralExpr("localValue"));
        builder.global("globalVar", new LiteralExpr("globalValue"));

        List<FtlNode> nodes = builder.build();
        assertEquals(2, nodes.size());
        assertTrue(nodes.get(0) instanceof LocalNode);
        assertTrue(nodes.get(1) instanceof GlobalNode);

        ValidationRecorder recorder = builder.getValidationRecorder();
        assertTrue(recorder.isAssigned("temp"));
        assertTrue(recorder.isAssigned("globalVar"));
    }

    @Test
    void testComment() {
        FtlBuilder builder = FtlBuilder.create(context);
        builder.comment("This is a comment");

        List<FtlNode> nodes = builder.build();
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof CommentNode);
        assertEquals("This is a comment", ((CommentNode) nodes.get(0)).getText());
    }

    @Test
    void testComplexTemplate() {
        FtlBuilder builder = FtlBuilder.create(context);

        builder
                .text("User: ")
                .var("user.name")
                .text(" (Age: ")
                .var("user.age")
                .text(")\n")
                .assign("total", "0")
                .list("order", "orders", listBuilder ->
                        listBuilder
                                .text("Order ")
                                .var("order.id")
                                .text(": $")
                                .var("order.amount")
                                .text("\n")
                                .assign("total", "total + order.amount")
                )
                .text("Total: $")
                .var("total");

        List<FtlNode> nodes = builder.build();
        assertEquals(7, nodes.size());

        // 验证变量引用记录
        ValidationRecorder recorder = builder.getValidationRecorder();
        List<VariableReference> refs = recorder.getReferences();
        assertTrue(refs.size() > 0);

        // 验证变量赋值
        assertTrue(recorder.isAssigned("total"));
    }

    @Test
    void testValidationRecorderIntegration() {
        FtlBuilder builder = FtlBuilder.create(context);

        // 使用未定义的变量应该被记录
        builder.var("undefinedVar");

        List<FtlNode> nodes = builder.build();

        // 验证变量引用被记录
        ValidationRecorder recorder = builder.getValidationRecorder();
        List<VariableReference> refs = recorder.getReferences();
        assertEquals(1, refs.size());
        assertEquals("undefinedVar", refs.get(0).getExpression());

        // 验证未定义变量检测（会输出警告但不会抛异常）
        assertFalse(recorder.isAssigned("undefinedVar"));
    }

    @Test
    void testScopeManagement() {
        FtlBuilder builder = FtlBuilder.create(context);

        // 测试 list 作用域
        builder.list("item", "items", listBuilder -> {
            // 在 list 作用域内使用局部变量
            listBuilder.var("item");
            // 在 list 作用域内赋值
            listBuilder.assign("itemIndex", "1");
        });

        List<FtlNode> nodes = builder.build();
        assertEquals(1, nodes.size());

        // 验证作用域管理
        ValidationRecorder recorder = builder.getValidationRecorder();
        assertTrue(recorder.isAssigned("itemIndex")); // 全局 assign 应该被记录
    }

    @Test
    void testAtLocationTracking() {
        FtlBuilder builder = FtlBuilder.create(context.at("custom.ftl"));
        builder.text("Hello").var("user.name");

        List<FtlNode> nodes = builder.build();
        assertEquals(2, nodes.size());

        // 验证变量引用包含位置信息
        ValidationRecorder recorder = builder.getValidationRecorder();
        List<VariableReference> refs = recorder.getReferences();
        assertEquals(1, refs.size());
        assertTrue(refs.get(0).getSource().contains("custom.ftl"));
    }

    @Test
    void testEmptyBuild() {
        FtlBuilder builder = FtlBuilder.create(context);
        List<FtlNode> nodes = builder.build();
        assertEquals(0, nodes.size());

        ValidationRecorder recorder = builder.getValidationRecorder();
        assertEquals(0, recorder.getReferences().size());
    }

    @Test
    void testMultipleBuildCalls() {
        FtlBuilder builder = FtlBuilder.create(context);
        builder.text("Hello");

        List<FtlNode> nodes1 = builder.build();
        List<FtlNode> nodes2 = builder.build();

        assertEquals(nodes1.size(), nodes2.size());
        assertNotSame(nodes1, nodes2); // 应该返回不同的不可变列表
    }

    @Test
    void testNestedStructures() {
        FtlBuilder builder = FtlBuilder.create(context);

        builder
                .ifBlock("user.age > 18", ifBuilder ->
                        ifBuilder
                                .list("order", "orders", listBuilder ->
                                        listBuilder
                                                .ifBlock("order.amount > 50", nestedIfBuilder ->
                                                        nestedIfBuilder.text("Large order\n")
                                                )
                                )
                );

        List<FtlNode> nodes = builder.build();
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof IfNode);
    }
}
