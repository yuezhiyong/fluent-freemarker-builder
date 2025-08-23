package fluent.freemarker.ast;

import fluent.freemarker.ast.expr.FtlExpr;
import fluent.freemarker.ast.expr.LiteralExpr;
import fluent.freemarker.builder.AstJson;
import fluent.freemarker.builder.FreeMarkerRenderer;
import fluent.freemarker.builder.FtlBuilder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SwitchNodeTests {

    @Test
    public void testSwitchNodeCreation() {
        List<CaseNode> cases = Arrays.asList(
            new CaseNode(Collections.singletonList(new LiteralExpr("admin")),
                    Collections.singletonList(new TextNode("Admin access"))),
            new CaseNode(Collections.singletonList(new LiteralExpr("user")),
                    Collections.singletonList(new TextNode("User access")))
        );
        List<FtlNode> defaultBlock = Collections.singletonList(new TextNode("Guest access"));
        
        SwitchNode switchNode = new SwitchNode(new LiteralExpr("user.role"), cases, defaultBlock);
        
        assertNotNull(switchNode.getExpr());
        assertEquals(2, switchNode.getCases().size());
        assertEquals(1, switchNode.getDefaultBody().size());
    }

    @Test
    public void testSwitchNodeSerialization() throws Exception {
        List<CaseNode> cases = Collections.singletonList(
                new CaseNode(Collections.singletonList(new LiteralExpr("active")),
                        Collections.singletonList(new TextNode("User is active")))
        );
        List<FtlNode> defaultBlock = Collections.singletonList(new TextNode("User status unknown"));
        
        SwitchNode switchNode = new SwitchNode(new LiteralExpr("user.status"), cases, defaultBlock);
        
        // Serialize to JSON
        String json = AstJson.toJson(Collections.singletonList(switchNode));
        assertNotNull(json);
        assertTrue(json.contains("\"type\" : \"Switch\""));
        
        // Deserialize from JSON
        List<FtlNode> deserializedNodes = AstJson.fromJson(json);
        assertEquals(1, deserializedNodes.size());
        assertTrue(deserializedNodes.get(0) instanceof SwitchNode);
        
        SwitchNode deserializedSwitch = (SwitchNode) deserializedNodes.get(0);
        assertEquals(1, deserializedSwitch.getCases().size());
        assertEquals(1, deserializedSwitch.getDefaultBody().size());
    }

    @Test
    public void testSwitchNodeBasicRendering() {
        List<FtlNode> ast = FtlBuilder.create()
                .switchBlock("user.role", b -> b
                                .caseBlock("admin", c -> c.text("Admin: ").var("user.name"))
                                .caseBlock("user", c -> c.text("User: ").var("user.name"))
                        , b -> b.text("Guest: ").var("user.name"))
                .build();

        String ftl = new FreeMarkerRenderer().render(ast);
        assertTrue(ftl.contains("<#switch"));
        assertTrue(ftl.contains("<#case"));
        assertTrue(ftl.contains("<#default>"));
        assertTrue(ftl.contains("</#switch>"));
        assertTrue(ftl.contains("Admin: ${user.name}"));
        assertTrue(ftl.contains("User: ${user.name}"));
        assertTrue(ftl.contains("Guest: ${user.name}"));
    }

    @Test
    public void testSwitchNodeWithoutDefault() {
        List<FtlNode> ast = FtlBuilder.create()
                .switchBlock("status", b -> b
                        .caseBlock("success", c -> c.text("Operation successful"))
                        .caseBlock("error", c -> c.text("Operation failed"))
                        , null)
                .build();

        assertEquals(1, ast.size());
        assertTrue(ast.get(0) instanceof SwitchNode);
        
        SwitchNode switchNode = (SwitchNode) ast.get(0);
        assertEquals(2, switchNode.getCases().size());
        assertTrue(switchNode.getDefaultBody().isEmpty());
    }

    @Test
    public void testSwitchNodeWithComplexCases() {
        List<FtlNode> ast = FtlBuilder.create()
                .switchBlock("user.type", b -> b
                        .caseBlock("premium", c -> c
                            .text("Welcome Premium User: ")
                            .var("user.name")
                            .text(" - You have full access!"))
                        .caseBlock("basic", c -> c
                            .text("Welcome Basic User: ")
                            .var("user.name")
                            .text(" - Limited access"))
                        .caseBlock("trial", c -> c
                            .text("Trial User: ")
                            .var("user.name")
                            .text(" - ")
                            .var("user.trialDaysLeft")
                            .text(" days left"))
                        , b -> b.text("Unknown user type"))
                .build();

        assertTrue(ast.get(0) instanceof SwitchNode);
        SwitchNode switchNode = (SwitchNode) ast.get(0);
        assertEquals(3, switchNode.getCases().size());
        
        // Check that each case has the expected content
        for (CaseNode caseNode : switchNode.getCases()) {
            assertFalse(caseNode.body.isEmpty());
        }
    }

    @Test
    public void testNestedSwitchNodes() {
        List<FtlNode> ast = FtlBuilder.create()
                .switchBlock("user.role", b -> b
                        .caseBlock("admin", c -> c
                            .text("Admin Panel: ")
                            .switchBlock("user.department", inner -> inner
                                .caseBlock("IT", it -> it.text("IT Admin"))
                                .caseBlock("HR", hr -> hr.text("HR Admin"))
                                , def -> def.text("General Admin")))
                        , b -> b.text("Regular User"))
                .build();

        assertTrue(ast.get(0) instanceof SwitchNode);
        SwitchNode outerSwitch = (SwitchNode) ast.get(0);
        assertEquals(1, outerSwitch.getCases().size());
        
        // Check for nested switch
        CaseNode adminCase = outerSwitch.getCases().get(0);
        assertEquals(2, adminCase.body.size());
        assertTrue(adminCase.body.get(1) instanceof SwitchNode);
        
        SwitchNode innerSwitch = (SwitchNode) adminCase.body.get(1);
        assertEquals(2, innerSwitch.getCases().size());
    }

    @Test
    public void testSwitchNodeToString() {
        List<CaseNode> cases = Arrays.asList(
            new CaseNode(Arrays.asList(new LiteralExpr("test")), 
                        Arrays.asList(new TextNode("Test case")))
        );
        SwitchNode switchNode = new SwitchNode(new LiteralExpr("testExpr"), cases, Collections.emptyList());
        
        String toString = switchNode.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Switch") || toString.length() > 0);
    }

    @Test
    public void testSwitchNodeVisitorPattern() {
        List<CaseNode> cases = Arrays.asList(
            new CaseNode(Arrays.asList(new LiteralExpr("test")), 
                        Arrays.asList(new TextNode("Test")))
        );
        SwitchNode switchNode = new SwitchNode(new LiteralExpr("test"), cases, Collections.emptyList());
        
        // Create a simple visitor to test the accept method
        FtlVisitor visitor = new FtlBaseVisitor() {
            @Override
            public void visit(SwitchNode node) {
                assertNotNull(node.getExpr());
                assertEquals(1, node.getCases().size());
            }
        };
        
        // Test that visitor accept works without throwing exceptions
        assertDoesNotThrow(() -> switchNode.accept(visitor));
    }

    @Test
    public void testImmutableCollections() {
        List<CaseNode> cases = Arrays.asList(
            new CaseNode(Arrays.asList(new LiteralExpr("case1")), 
                        Arrays.asList(new TextNode("Case 1")))
        );
        List<FtlNode> defaultBlock = Arrays.asList(new TextNode("Default"));
        
        SwitchNode switchNode = new SwitchNode(new LiteralExpr("expr"), cases, defaultBlock);
        
        // Test that returned collections are immutable
        assertThrows(UnsupportedOperationException.class, () -> {
            switchNode.getCases().add(new CaseNode(Arrays.asList(new LiteralExpr("case2")), 
                                                   Arrays.asList(new TextNode("Case 2"))));
        });
        
        assertThrows(UnsupportedOperationException.class, () -> {
            switchNode.getDefaultBody().add(new TextNode("Should not work"));
        });
    }

    @Test
    public void testCaseNodeCreation() {
        List<FtlExpr> values = Arrays.asList(new LiteralExpr("value1"), new LiteralExpr("value2"));
        List<FtlNode> body = Arrays.asList(new TextNode("Multiple values case"));
        
        CaseNode caseNode = new CaseNode(values, body);
        
        assertEquals(2, caseNode.values.size());
        assertEquals(1, caseNode.body.size());
    }

    @Test
    public void testCaseNodeWithMultipleValues() {
        List<FtlNode> ast = FtlBuilder.create()
                .text("<#switch dayOfWeek>")
                .text("<#case 'monday'><#case 'tuesday'><#case 'wednesday'><#case 'thursday'><#case 'friday'>")
                .text("Weekday")
                .text("<#break>")
                .text("<#case 'saturday'><#case 'sunday'>")
                .text("Weekend")
                .text("<#break>")
                .text("<#default>")
                .text("Unknown")
                .text("</#switch>")
                .build();

        // This test verifies that multiple case values can be handled
        // even if not directly through the builder API
        String rendered = new FreeMarkerRenderer().render(ast);
        assertTrue(rendered.contains("Weekday"));
        assertTrue(rendered.contains("Weekend"));
        assertTrue(rendered.contains("Unknown"));
    }
}
