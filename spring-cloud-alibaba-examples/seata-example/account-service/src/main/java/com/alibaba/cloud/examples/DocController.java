/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.examples;

import com.alibaba.cloud.examples.common.provider.CacheProvider;
import com.alibaba.cloud.examples.customer.Customer;
import com.alibaba.cloud.examples.customer.Person;
import com.alibaba.cloud.examples.service.mail.MailService;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import io.seata.config.Configuration;
import io.seata.config.ConfigurationFactory;
import io.seata.core.context.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.util.*;

import com.alibaba.cloud.examples.common.util.http.client.HttpClient;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author xiaojing
 */
@RestController
//如果需要对 Bean 进行动态刷新，请参照 Spring 和 Spring Cloud 规范。推荐给类添加 @RefreshScope 或 @ConfigurationProperties 注解，
@RefreshScope
public class DocController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DocController.class);

	private static final String SUCCESS = "SUCCESS";

	private static final String FAIL = "FAIL";

	private final JdbcTemplate jdbcTemplate;

	private Random random;

	@Autowired
	private Environment environment;//获取配置数据，例如：environment.getProperty("server.port")

	@Autowired
	UserConfig userConfig;

	@Autowired
	private NacosConfigManager nacosConfigManager;

	@Value("${user.name:zz}")
	String userName;

	@Value("${user.age:25}")
	Integer age;

//	@Autowired
//	private RedisTemplate<String, String> redisTemplate;

	private final RestTemplate restTemplate;

	private static final Configuration CONFIG = ConfigurationFactory.getInstance();

	public DocController(JdbcTemplate jdbcTemplate, RestTemplate restTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.random = new Random();
		this.restTemplate = restTemplate;
	}

	@Autowired DataInitializer initializer;

	@GetMapping(value = "/testConfig")
	public String testConfig() {

		LOGGER.info("========="+environment.getProperty("user.id")+"============");

		return environment.getProperty("user.id");
	}

	@RequestMapping("/user")
	public String simple() {

		return "Hello Nacos Config!" + "Hello " + userName + " " + age + " [UserConfig]: "
				+ userConfig + "!" + nacosConfigManager.getConfigService();
	}

	@RequestMapping("/getUser")
	@Cacheable(value="user-key")
	public Customer getUser() {
		Customer user=new Customer("Dave", "Matthews");
		System.out.println("若下面没出现“无缓存的时候调用”字样且能打印出数据表示测试成功");
		return user;
	}


	@RequestMapping("/uid")
	String uid(HttpSession session) {
		UUID uid = (UUID) session.getAttribute("uid");
		if (uid == null) {
			uid = UUID.randomUUID();
		}
		session.setAttribute("uid", uid);
		return session.getId();
	}

	@RequestMapping("/uid1")
	Object uid1(HttpSession session) {
		UUID uid = (UUID) session.getAttribute("uid");
		if (uid == null) {
			uid = UUID.randomUUID();
			System.out.println(uid);
			session.setAttribute("uid", uid);
		}

		return session.getAttribute("uid");
	}

	@ResponseBody
	@RequestMapping("index")
	public String index(){

		String str = "";

		str += CacheProvider.set("tyh", "aaaaaaaaaaaaaaaaaa");
		str += "|";
		str += CacheProvider.get("tyh");
		str += "|";
		//str += CacheProvider.del("tyh");

		str += "|||";

		Cookie cookie = new Cookie("aaa", "bbb");
		str += CacheProvider.set("cookie", cookie);
		str += "|";
		str += CacheProvider.get("cookie", Cookie.class).getName();
		str += "|";
		//str += CacheProvider.del("cookie");
//
		return str.toString();
	}

	@RequestMapping("doc")
	public String doc(){

		/**
		 * https://www.cnblogs.com/dolphin0520/p/3736238.html
		 * final 表示最终的 （即一旦赋值 就不可以改变）
		 * 对于一个final变量，如果是基本数据类型的变量，则其数值一旦在初始化之后便不能更改；如果是引用类型的变量，则在对其初始化之后便不能再让其指向另一个对象。
		 * 当用final作用于类的成员变量时，成员变量（注意是类的成员变量，局部变量只需要保证在使用之前被初始化赋值即可）必须在定义时或者构造器中进行初始化赋值，而且final变量一旦被初始化赋值之后，就不能再被赋值了。
		 *
		 * 当final变量是基本数据类型以及String类型时，如果在编译期间能知道它的确切值，则编译器会把它当做编译期常量使用。
		 * 也就是说在用到该final变量的地方，相当于直接访问的这个常量，不需要在运行时确定。
		 * 因此在以下的一段代码中，由于变量b被final修饰，因此会被当做编译器常量，所以在使用到b的地方会直接将变量b 替换为它的  值。
		 * 而对于变量d的访问却需要在运行时通过链接来进行。想必其中的区别大家应该明白了，不过要注意，只有在编译期间能确切知道final变量值的情况下，编译器才会进行这样的优化
		 *
		 */
		String a = "hello2";
		final String b = "hello";//
		String d = "hello";
		String c = b + 2;
		String e = d + 2;

		System.out.println(c);
		System.out.println(e);

		System.out.println((a == c));
		System.out.println((a == e));

		/**
		 * 比如下面的这段代码就不会进行优化
		 * public class Test {
		 *     public static void main(String[] args)  {
		 *         String a = "hello2";
		 *         final String b = getHello();
		 *         String c = b + 2;
		 *         System.out.println((a == c));
		 *
		 *     }
		 *
		 *     public static String getHello() {
		 *         return "hello";
		 *     }
		 * }
		 * 这段代码的输出结果为false。
		 */

		/**
		 * 引用变量被final修饰之后，虽然不能再指向其他对象，但是它指向的对象的内容是可变的
		 * public class Test {
		 *     public static void main(String[] args)  {
		 *         final MyClass myClass = new MyClass();
		 *         System.out.println(++myClass.i);
		 *
		 *     }
		 * }
		 *
		 * class MyClass {
		 *     public int i = 0;
		 * }
		 * 这段代码可以顺利编译通过并且有输出结果，输出结果为1。这说明引用变量被final修饰之后，虽然不能再指向其他对象，但是它指向的对象的内容是可变的
		 */

		/**
		 * final （作用域是 对象或者局域 ） 和static（作用域是 类 ）
		 * static作用于成员变量用来表示只保存一份副本，而final的作用是用来保证变量不可变
		 * public class Test {
		 *     public static void main(String[] args)  {
		 *         MyClass myClass1 = new MyClass();
		 *         MyClass myClass2 = new MyClass();
		 *         System.out.println(myClass1.i);
		 *         System.out.println(myClass1.j);
		 *         System.out.println(myClass2.i);
		 *         System.out.println(myClass2.j);
		 *
		 *     }
		 * }
		 *
		 * class MyClass {
		 *     public final double i = Math.random();
		 *     public static double j = Math.random();
		 * }
		 * 运行这段代码就会发现，每次打印的两个j值都是一样的，而i的值却是不同的。从这里就可以知道final和static变量的区别了
		 */

		/**
		 * 关于final参数的问题
		 * java参数传递采用的是值传递，对于基本类型的变量，相当于直接将变量进行了拷贝。所以即使没有final修饰的情况下，在方法内部改变了变量i的值也不会影响方法外的i
		 * public class Test {
		 *     public static void main(String[] args)  {
		 *         MyClass myClass = new MyClass();
		 *         StringBuffer buffer = new StringBuffer("hello");
		 *         myClass.changeValue(buffer);
		 *         System.out.println(buffer.toString());
		 *     }
		 * }
		 *
		 * class MyClass {
		 *
		 *     void changeValue(final StringBuffer buffer) {
		 *         buffer.append("world");
		 *     }
		 * }
		 * 运行这段代码就会发现输出结果为 helloworld。
		 * 很显然，用final进行修饰并没有阻止在changeValue中改变buffer指向的对象的内容。
		 * 有人说假如把final去掉了，万一在changeValue中让buffer指向了其他对象怎么办。
		 * 有这种想法的朋友可以自己动手写代码试一下这样的结果是什么，如果把final去掉了，然后在changeValue中让buffer指向了其他对象，也不会影响到main方法中的buffer，
		 * 原因在于java采用的是值传递，对于引用变量，传递的是引用的值，也就是说让实参和形参同时指向了同一个对象，因此让形参重新指向另一个对象对实参并没有任何影响。
		 */

		return e;
	}

	// inject the actual template
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	// inject the template as ListOperations
	// can also inject as Value, Set, ZSet, and HashOperations
//	@Resource(name="redisTemplate")
//	private ListOperations<String, String> listOps;

	public void addLink(String userId, String url) {
		//listOps.leftPush(userId, url.toExternalForm());
		// or use template directly
		//redisTemplate.boundListOps(userId).leftPush(url.toExternalForm());

		redisTemplate.boundListOps(userId).leftPush(url);

		System.out.println(redisTemplate.boundListOps(userId).rightPop());

		redisTemplate.boundSetOps("set").add("966");

		redisTemplate.boundZSetOps("zset").add("8989",10);

		redisTemplate.boundHashOps("hash").put("hk","hv555");

		Map<String,String> var1 = new HashMap<>();
		var1.put("a","a1");
		var1.put("b","b1");
		var1.put("c","c1");
		var1.put("d","d1");
		redisTemplate.boundHashOps("hash1").putAll(var1);

		System.out.println(redisTemplate.boundHashOps("hash1").entries().get("a"));
		System.out.println(redisTemplate.boundHashOps("hash1").entries().get("b"));

		//redisTemplate.opsForValue().set("test",new Person("Homer", "Simpson").toString());

		CacheProvider.set("test",new Person("Homer", "Simpson"));
		System.out.println(CacheProvider.get("test",Person.class).getFirstname());


	}


	@Autowired
	ReactiveRedisOperations<String, Person> typedOperations;

	@Autowired ReactiveRedisOperations<String, Object> genericOperations;

	@RequestMapping("redis")
	public String redis(){
		addLink("555","http://www.baidu.com1");


//		StepVerifier.create(typedOperations.opsForValue().set("homer", new Person("Homer", "Simpson"))) //
//				.expectNext(true) //
//				.verifyComplete();
//
//		System.out.println(typedOperations.execute(conn -> conn.stringCommands().get(ByteBuffer.wrap("homer".getBytes()))) //
//				.map(ByteUtils::getBytes) //
//				.map(String::new)
//		);

//		get.as(StepVerifier::create) //
//				.expectNext("{\"firstname\":\"Homer\",\"lastname\":\"Simpson\"}") //
//				.verifyComplete();
//
//		typedOperations.opsForValue().get("homer").as(StepVerifier::create) //
//				.expectNext(new Person("Homer", "Simpson")) //
//				.verifyComplete();

		return "redis";
	}

	/**
	 * @PathVaribale 获取url中的数据
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/test2/{id}/{name}",  produces = "application/json")
	public Map test2(@PathVariable("id") Integer id,@PathVariable Map map) {

		LOGGER.info(map.toString());
		LOGGER.info(id.toString());

		return map;
	}

	/**
     * RequestParam 获取请求参数的值(包括 get、post、put、delete 请求参数)
	 * @param map
     * @return
     */
	@RequestMapping(value = "/test1",  produces = "application/json")
	//required=false 表示url中可以无id参数，此时就使用默认参数
	public Map test1(@RequestParam(value="id",required = false,defaultValue = "1") Integer id,@RequestParam("userId") Integer id1, @RequestParam Map map) {

		LOGGER.info(id.toString());
		LOGGER.info(id1.toString());
		LOGGER.info(map.toString());

		return map;
	}

	/**
	 * RequestBody 请求数据必须是json格式数据
	 * @param map
	 * @return
	 */
	//@PostMapping(value = "/test", produces = "application/json")
	@RequestMapping(value = "/test",  produces = "application/json")
	public Map test(@RequestBody Map map) {

		LOGGER.info(map.toString());
		LOGGER.info(map.get("store_id").toString());

		return map;
	}

	@RequestMapping(value = "/restTemplate",  produces = "application/json")
	public Map restTemplate() {

		String url = "http://192.168.152.128:82/api/common/dict/select";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		map.add("type", "country");
		//map.add("money", "666");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(
				map, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(url, request,
				String.class);

		LOGGER.info(map.toString());
		LOGGER.info(response.getStatusCode().toString());
		LOGGER.info(response.getStatusCodeValue()+"");
		LOGGER.info(response.getHeaders().toString());
		LOGGER.info(response.getBody());

		Gson gson = new Gson();
		Map res = gson.fromJson(response.getBody(), Map.class);

		List<Map> data = (List<Map>) res.get("data");
		LOGGER.info(data.toString());

//		LOGGER.info(data.get(0).get("id")+"");
//		LOGGER.info(data.get(0).get("dict_key")+"");
//		LOGGER.info(data.get(0).get("dict_value")+"");

		for (Map item : data) {
			LOGGER.info(item.get("id")+"");
			LOGGER.info(item.get("dict_key")+"");
			LOGGER.info(item.get("dict_value")+"");
		}

		return map;
	}

	@RequestMapping(value = "/netty",  produces = "application/json")
	public String netty() {
		HttpClient.start("192.168.152.128",82);
		return "";
	}

	@Autowired
	private MailService mailService;

	@Autowired
	private TemplateEngine templateEngine;

	@RequestMapping(value = "/testSimpleMail",  produces = "application/json")
	public void testSimpleMail() throws Exception {
		mailService.sendSimpleMail("Jmiy_cen@patazon.net","test simple mail====="," hello this is simple mail");
	}

	@RequestMapping(value = "/testHtmlMail",  produces = "application/json")
	public void testHtmlMail() throws Exception {
		String content="<html>\n" +
				"<body>\n" +
				"    <h3>hello world ! 这是一封html邮件!</h3>\n" +
				"</body>\n" +
				"</html>";
		mailService.sendHtmlMail("Jmiy_cen@patazon.net","test simple mail",content);
	}

	@RequestMapping(value = "/sendAttachmentsMail",  produces = "application/json")
	public void sendAttachmentsMail() {
		String filePath="E:\\Work\\patozon\\spring\\Dingtalk_20210623111601.jpg";
		mailService.sendAttachmentsMail("Jmiy_cen@patazon.net", "主题：带附件的邮件", "有附件，请查收！", filePath);
	}


	@RequestMapping(value = "/sendInlineResourceMail",  produces = "application/json")
	public void sendInlineResourceMail() {
		String rscId = "neo006";
		String content="<html><body>这是有图片的邮件：<img src=\'cid:" + rscId + "\' ></body></html>";
		String imgPath = "E:\\Work\\patozon\\spring\\Dingtalk_20210623111601.jpg";

		mailService.sendInlineResourceMail("Jmiy_cen@patazon.net", "主题：这是有图片的邮件", content, imgPath, rscId);
	}


	@RequestMapping(value = "/sendTemplateMail",  produces = "application/json")
	public void sendTemplateMail() {
		//创建邮件正文
		Context context = new Context();
		context.setVariable("id", "006");
		String emailContent = templateEngine.process("emailTemplate", context);

		mailService.sendHtmlMail("Jmiy_cen@patazon.net","主题：这是模板邮件========",emailContent);
	}



}
