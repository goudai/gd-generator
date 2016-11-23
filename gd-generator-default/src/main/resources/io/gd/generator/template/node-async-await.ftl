let {Client} = require('dubbo-node-yield')
	, client = new Client({'level': 'debug'})

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
		await execute${export.serviceName}('${method.name}', body)
	},
	<#else>
	${method.name}: async body => {
		await execute${export.serviceName}('${method.name}', body)
	}
	</#if>
</#list>
}
</#list>