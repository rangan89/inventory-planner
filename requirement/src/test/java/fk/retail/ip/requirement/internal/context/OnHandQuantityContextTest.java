package fk.retail.ip.requirement.internal.context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OnHandQuantityContextTest {

    OnHandQuantityContext onHandQuantityContext;

    @Before
    public void setup() {
        onHandQuantityContext = new OnHandQuantityContext();
        onHandQuantityContext.addInventoryQuantity("fsn1", "wh1", 1, 2);
        onHandQuantityContext.addInventoryQuantity("fsn1", "wh2", 3, 4);
        onHandQuantityContext.addIwtQuantity("fsn1", "wh1", 5);
        onHandQuantityContext.addIwtQuantity("fsn1", "wh2", 6);
        onHandQuantityContext.addOpenRequirementAndPurchaseOrder("fsn1", "wh1", 7, 8);
        onHandQuantityContext.addOpenRequirementAndPurchaseOrder("fsn1", "wh2", 9, 10);
    }

    @Test
    public void testGetters() {
        double actual = onHandQuantityContext.getInventoryQuantity("fsn1", "wh1");
        Assert.assertEquals(1, actual, 0.01);
        actual = onHandQuantityContext.getOnHandInventoryQuantity("fsn1", "wh1");
        Assert.assertEquals(2, actual, 0.01);
        actual = onHandQuantityContext.getIwtQuantity("fsn1", "wh1");
        Assert.assertEquals(5, actual, 0.01);
        actual = onHandQuantityContext.getOpenRequirementQuantity("fsn1", "wh1");
        Assert.assertEquals(7, actual, 0.01);
        actual = onHandQuantityContext.getPendingPurchaseOrderQuantity("fsn1", "wh1");
        Assert.assertEquals(8, actual, 0.01);
        actual = onHandQuantityContext.getTotalQuantity("fsn1", "wh1");
        Assert.assertEquals(21, actual, 0.01);
        actual = onHandQuantityContext.getTotalQuantity("fsn1");
        Assert.assertEquals(49, actual, 0.01);
        //for wh not present
        actual = onHandQuantityContext.getInventoryQuantity("fsn1", "wh3");
        Assert.assertEquals(0, actual, 0.01);
        //for fsn not present
        actual = onHandQuantityContext.getIwtQuantity("fsn3", "wh1");
        Assert.assertEquals(0, actual, 0.01);
        //for both fsn wh not present
        actual = onHandQuantityContext.getOpenRequirementQuantity("fsn3", "wh3");
        Assert.assertEquals(0, actual, 0.01);
    }

    @Test
    public void testAdd() {
        onHandQuantityContext.addInventoryQuantity("fsn1", "wh1", 1, 2);
        onHandQuantityContext.addInventoryQuantity("fsn1", "wh2", 3, 4);
        onHandQuantityContext.addIwtQuantity("fsn1", "wh1", 5);
        onHandQuantityContext.addIwtQuantity("fsn1", "wh2", 6);
        onHandQuantityContext.addOpenRequirementAndPurchaseOrder("fsn1", "wh1", 7, 8);
        onHandQuantityContext.addOpenRequirementAndPurchaseOrder("fsn1", "wh2", 9, 10);

        double actual = onHandQuantityContext.getInventoryQuantity("fsn1", "wh1");
        Assert.assertEquals(1, actual, 0.01);
        actual = onHandQuantityContext.getOnHandInventoryQuantity("fsn1", "wh1");
        Assert.assertEquals(2, actual, 0.01);
        actual = onHandQuantityContext.getIwtQuantity("fsn1", "wh1");
        Assert.assertEquals(10, actual, 0.01);
        actual = onHandQuantityContext.getOpenRequirementQuantity("fsn1", "wh1");
        Assert.assertEquals(7, actual, 0.01);
        actual = onHandQuantityContext.getPendingPurchaseOrderQuantity("fsn1", "wh1");
        Assert.assertEquals(8, actual, 0.01);
        actual = onHandQuantityContext.getTotalQuantity("fsn1", "wh1");
        Assert.assertEquals(26, actual, 0.01);
        actual = onHandQuantityContext.getTotalQuantity("fsn1");
        Assert.assertEquals(60, actual, 0.01);
    }
}
