/**
 * 
 */
package cn.bc.core.query.condition.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;


/**
 * 
 * @author dragon
 * 
 */
public class InConditionTest {
	@Test
	public void testArrayValues() {
		//包含1个元素的数组
		Long[] ids = new Long[]{new Long(1)};
		InCondition c = new InCondition("key", ids);
		Assert.assertEquals("key in (?)", c.getExpression());
		Assert.assertNotNull(c.getValues());
		Assert.assertTrue(c.getValues().size() == 1);
		Assert.assertEquals(new Long(1), c.getValues().get(0));

		//包含2个元素的数组
		ids = new Long[]{new Long(1),new Long(2)};
		c = new InCondition("key", ids);
		Assert.assertEquals("key in (?,?)", c.getExpression());
		Assert.assertNotNull(c.getValues());
		Assert.assertTrue(c.getValues().size() == 2);
		Assert.assertEquals(new Long(1), c.getValues().get(0));
		Assert.assertEquals(new Long(2), c.getValues().get(1));
	}
	
	@Test
	public void testCollectionValues() {
		//包含1个元素的集合
		List<Long> ids = new ArrayList<Long>();
		ids.add(new Long(1));
		InCondition c = new InCondition("key", ids);
		Assert.assertEquals("key in (?)", c.getExpression());
		Assert.assertNotNull(c.getValues());
		Assert.assertTrue(c.getValues().size() == 1);
		Assert.assertEquals(new Long(1), c.getValues().get(0));

		//包含2个元素的数组
		ids.add(new Long(2));
		c = new InCondition("key", ids);
		Assert.assertEquals("key in (?,?)", c.getExpression());
		Assert.assertNotNull(c.getValues());
		Assert.assertTrue(c.getValues().size() == 2);
		Assert.assertEquals(new Long(1), c.getValues().get(0));
		Assert.assertEquals(new Long(2), c.getValues().get(1));
	}
	
	@Test
	public void testCollectionValuesWithAlias() {
		//包含1个元素的集合
		List<Long> ids = new ArrayList<Long>();
		ids.add(new Long(1));
		InCondition c = new InCondition("key", ids);
		Assert.assertEquals("alias_.key in (?)", c.getExpression("alias_"));
		Assert.assertNotNull(c.getValues());
		Assert.assertTrue(c.getValues().size() == 1);
		Assert.assertEquals(new Long(1), c.getValues().get(0));

		//包含2个元素的数组
		ids.add(new Long(2));
		c = new InCondition("key", ids);
		Assert.assertEquals("alias_.key in (?,?)", c.getExpression("alias_"));
		Assert.assertNotNull(c.getValues());
		Assert.assertTrue(c.getValues().size() == 2);
		Assert.assertEquals(new Long(1), c.getValues().get(0));
		Assert.assertEquals(new Long(2), c.getValues().get(1));
	}
}
