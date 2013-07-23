<%@ page import="org.chai.memms.util.Utils" %>
<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="org.chai.memms.security.User" %>
<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderType" %>
<%@ page import="org.chai.memms.preventive.maintenance.DurationBasedOrder" %>
<table class="items">
	<thead>
		<tr>
			<g:if test="${reportTypeOptions.contains('location')}">
				<g:sortableColumn property="dataLocation"  title="${message(code: 'location.label')}" params="[q:q]" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('code')}">
				<th><g:message code="default.equipment.code.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('serialNumber')}">
				<th><g:message code="default.equipment.serial.number.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('equipmentType')}">
				<th><g:message code="default.equipment.type.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('model')}">
				<g:sortableColumn property="model"  title="${message(code: 'equipment.model.label')}" params="[q:q]" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('manufacturer')}">
				<g:sortableColumn property="manufacturer"  title="${message(code: 'provider.type.manufacturer')}" params="[q:q]" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('currentStatus')}">
				<g:sortableColumn property="status"  title="${message(code: 'entity.status.label')}" params="[q:q]" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('nextInterventionScheduledOn')}">
				<th><g:message code="listing.report.preventive.schedule.next.interventions.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('percentageInterventionsDone')}">
				<th><g:message code="listing.report.preventive.percentage.interventions.done.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('recurrencePeriod')}">
				<th><g:message code="listing.report.preventive.reoccurrence.period.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('startDate')}">
				<th><g:message code="listing.report.preventive.start.date.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('responsible')}">
				<th><g:message code="listing.report.preventive.responsible.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('actionName')}">
				<th><g:message code="listing.report.preventive.action.name.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('actionDescription')}">
				<th><g:message code="listing.report.preventive.action.description.label"/></th>
			</g:if>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="order">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<g:if test="${reportTypeOptions.contains('location')}">
					<td>${order.equipment.dataLocation.names}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('code')}">
					<td>${order.equipment.code}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('serialNumber')}">
					<td>${order.equipment.serialNumber}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('equipmentType')}">
					<td>${order.equipment.type.names}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('model')}">
					<td>${order.equipment.model}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('manufacturer')}">
					<td>${order.equipment.manufacturer?.contact?.contactName}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('currentStatus')}">
					<td>${message(code: order.status?.messageCode+'.'+order.status?.name)}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('nextInterventionScheduledOn')}">
					%{-- TODO AR calculation AFTER RELEASE --}%
					<td></td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('percentageInterventionsDone')}">
					%{-- TODO AR calculation AFTER RELEASE --}%
					<td></td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('recurrencePeriod')}">
					%{-- TODO AR calculation AFTER RELEASE --}%
					<td></td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('startDate')}">
					<td>${order.firstOccurenceOn?.timeDate}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('responsible')}">
					<td>${order.preventionResponsible}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('actionName')}">
					<td>${order.names}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('actionDescription')}">
					<td>${order.description}</td>
				</g:if>
			</tr>
		</g:each>
	</tbody>
</table>
<g:render template="/templates/pagination" />