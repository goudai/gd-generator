let {Client} = require('./dubbo-node-async-await')
	, client = exports.client = new Client({'level': 'debug'})

<#list node.services as service>
let execute${service.simpleName} = async(method, body) => {
	return await client.execute('${service.name}', method, body);
}
</#list>
<#list node.exports as export>
exports.${export.serviceName?uncap_first} = {
<#list export.methods as method>
	/*body : ${method.json}*/
	<#if method_has_next>
	${method.name}: async body => {
		return await execute${export.serviceName}('${method.name}', body)
	},
	<#else>
	${method.name}: async body => {
		return await execute${export.serviceName}('${method.name}', body)
	}
	</#if>
</#list>
}
</#list>