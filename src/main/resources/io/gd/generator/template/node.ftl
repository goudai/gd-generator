let {Client} = require('dubbo-node')
,client = new Client({'level':'error'})


<#list node.services as service>
let execute${service.simpleName} = (method, body) => {
		return client.execute('${service.name}',method,body)
}
</#list>

<#list node.exports as export>
exports.${export.serviceName?uncap_first} = {
	<#list export.methods as method>
	/*
		body : ${method.json}
	*/
	<#if method_has_next>
	${method.name}: body => execute${export.serviceName}('${method.name}',body),
	<#else>
	${method.name}: body => execute${export.serviceName}('${method.name}',body)
	</#if>
	</#list>
}
</#list>







