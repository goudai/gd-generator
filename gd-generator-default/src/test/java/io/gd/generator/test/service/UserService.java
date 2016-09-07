package io.gd.generator.test.service;

import io.gd.generator.test.entity.User;

/**
 * Created by freeman on 16/6/21.
 */
public interface UserService {
	/**
	 * 以下是基于springmvc注解,实现rest
	 */

	public User findById(Integer id);

	public User register(User user);


	/**
	 * 以下是自动生成url
	 */

	// http://localhost:8090?service=userService&method=getById&id=1
	public User getById(Integer id);


	// http://localhost:8090?service=userService&method=deleteById&id=1
	public void deleteById(Integer id);

	// http://localhost:8090?service=userService&method=deleteById&id=1&username=wuyu&password=1
	public Integer insert(User user);


	// http://localhost:8090?service=userService&method=testException
	public void testException(Integer id);


	// http://localhost:8090?service=userService&testErrorMsgException
	public void testErrorMsgException(Integer id);
}
