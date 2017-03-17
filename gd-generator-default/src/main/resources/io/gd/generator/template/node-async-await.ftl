const {Client} = require('./DubboNodeAsyncAwait')
	, client = exports.client = new Client({'level': 'debug'})

<#list node.services as service>
const execute${service.simpleName} = async(method, body) => await client.execute('${service.name}', method, body);
</#list>

<#list node.exports as export>
/***************************======>> ${export.serviceName?uncap_first} <<======***************************************/
exports.${export.serviceName?uncap_first} = {
<#list export.methods as method>
					/*body => ${method.json}*/
	<#if method_has_next>
	${method.name}: async body => await execute${export.serviceName}('${method.name}', body),
	<#else>
	${method.name}: async body => await execute${export.serviceName}('${method.name}', body)
	</#if>
</#list>
}
</#list>