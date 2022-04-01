package us.codecraft.webmagic.model;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/** 
 * @author Medeng Matthieu
 */
public class HttpRequestBodyTest {
	
	@Test
	public void testHttpRequestBodyCreation () {
		byte[] body = new byte[5];
		HttpRequestBody hrb = new HttpRequestBody(body, "A", "B");
		assertSame(body, hrb.getBody());
		assertEquals("A", hrb.getContentType());
		assertEquals("B", hrb.getEncoding());
		byte[] body2 = new byte[6];
		hrb.setBody(body2);
		hrb.setContentType("C");
		hrb.setEncoding("D");
		assertSame(body2, hrb.getBody());
		assertEquals("C", hrb.getContentType());
		assertEquals("D", hrb.getEncoding());
	}
	
	@Test
	public void testCustomMethod () {
		byte[] body = new byte[5];
		HttpRequestBody hrb = new HttpRequestBody(body, "A", "B");
		byte[] body2 = new byte[6];
		HttpRequestBody hrb2 = hrb.custom(body2, "C", "D");
		assertSame(body2, hrb2.getBody());
		assertEquals("C", hrb2.getContentType());
		assertEquals("D", hrb2.getEncoding());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFormIllegalArgumentException () throws IllegalArgumentException {
		byte[] body = new byte[5];
		HttpRequestBody hrb = new HttpRequestBody(body, "A", "UTF-8");
		Map<String,Object> params = new HashMap<>();
		try {
			hrb.form(params, "UTF-8");
		}
		catch (IllegalArgumentException e) {
			fail();
		}
		hrb.form(params, "E");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testXmlIllegalArgumentException () throws IllegalArgumentException {
		byte[] body = new byte[5];
		HttpRequestBody hrb = new HttpRequestBody(body, "A", "UTF-8");
		try {
			hrb.xml("xml", "UTF-8");
		}
		catch (IllegalArgumentException e) {
			fail();
		}
		hrb.xml("xml", "E");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testJsonIllegalArgumentException () throws IllegalArgumentException {
		byte[] body = new byte[5];
		HttpRequestBody hrb = new HttpRequestBody(body, "A", "UTF-8");
		try {
			hrb.json("json", "UTF-8");
		}
		catch (IllegalArgumentException e) {
			fail();
		}
		hrb.json("json", "E");
	}

}
